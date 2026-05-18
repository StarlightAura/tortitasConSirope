package org.tortitas.tfg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tortitas.tfg.models.Game;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 *Servicio principal encargado de la logica de negocio de los videojuegos y la IA.
 *<p>
 *Este servicio centraliza toda la interaccion con el Vector Store de MongoDB Atlas
 *y el modelo local de Ollama. Se encarga de transformar nuestro modelo de datos tradicional
 *al formato que entiende la IA, gestionar las cargas masivas desde archivos JSON y realizar
 *las busquedas semanticas por similitud.
 *</p>
 *@author Prabhnoor Singh Kaur
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@Service
public class GameService {
    @Autowired private MongoDBAtlasVectorStore vectorStore;
    @Autowired private OllamaEmbeddingModel ollamaEmbeddingModel;

    /**
     * Pide directamente al modelo de Ollama que calcule el embedding de un documento.
     * @param document El documento con el texto que queremos procesar.
     * @return Un array de floats con el vector generado.
     */
    public float[] obtenerEmbedding(Document document) {
        return ollamaEmbeddingModel.embed(document);
    }

    /**
     *Metdo auxiliar para transformar un objeto de tipo Game a un Document de Spring AI.
     *<p>
     *Mapea todas las propiedades de nuestro juego dentro del mapa de metadatos del Documento
     * y le pone filtros de seguridad para evitar nulos.
     *Esto permite que la IA pueda usar estos campos para filtrar o dar contexto mas tarde.
     *</p>
     *@param game El objeto juego de nuestra aplicacion con sus datos rellenos.
     *@return Un objeto {@link Document} listo para ser vectorizado.
     */
    public Document game2document(Game game){
        //Controlamos los nulos uno a uno para que la base de datos vectorial no de errores raros
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("sid", game.sid);
        metadata.put("name", game.name != null ? game.name : "");
        metadata.put("store_url", game.store_url != null ? game.store_url : "");
        metadata.put("genres", game.genres != null ? game.genres : List.of());
        metadata.put("tags", game.tags != null ? game.tags : List.of());
        metadata.put("store_uscore", game.store_uscore != null ? game.store_uscore : 0);
        metadata.put("full_price", game.full_price != null ? game.full_price : 0);
        metadata.put("description", game.description != null ? game.description : "");
        metadata.put("developers", game.developers != null ? game.developers : List.of());

        //El primer parametro es el contenido de texto principal que la IA analizara
        return new Document(game.name, metadata);
    }

    /**
     *Lee el archivo JSON con nuestro catalogo de videojuegos y lo sube en lotes a MongoDB Atlas.
     *<p>
     *Se utiliza Jackson para parsear el archivo, que convierte cada juego a un Document de Spring AI
     *y los va insertando en porciones fijas (llamadas batches) de 5 en 5. De esta forma podiamos ir
     * controlando como se iba subiendo.
     *</p>
     *@param rutaJson ruta del archivo .json.
     *@throws Exception Si el archivo no existe o hay problemas de formato al leerlo.
     */
    public void cargarJuegosDesdeJson(String rutaJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        //Deserializamos el JSON a una lista de objetos Game usando Jackson
        List<Game> juegos = mapper.readValue(
                new File(rutaJson),
                new TypeReference<List<Game>>() {}
        );
        System.out.println("Juegos leídos del JSON: " + juegos.size());

        //Transformamos toda la lista de juegos a Documentos usando Streams
        List<Document> docs = juegos.stream()
                .map(this::game2document)
                .collect(Collectors.toList());

        System.out.println("Total docs a insertar: " + docs.size());
        //Procesamos la insercion por lotes pequeños
        int batchSize = 5;
        for (int i = 0; i < docs.size(); i += batchSize) {
            //Troceamos la lista principal calculando dinamicamente el limite del lote actual
            List<Document> lote = docs.subList(i, Math.min(i + batchSize, docs.size()));
            try {
                //El vectorStore calcula el embedding con Ollama de forma implicita y lo guarda en MongoDB Atlas
                vectorStore.add(lote);
                System.out.println("Insertados " + i + " - " + (i + lote.size()));
            } catch (Exception e) {
                System.out.println("ERROR " + i + ": " + e.getMessage());
                e.printStackTrace();
                break; //Si da un error, paramos el bucle para evitar bucles de fallos
            }
        }
    }

    /**
     *Guarda y vectoriza un unico videojuego validando que cumpla los requisitos minimos.
     *<p>
     *Comprueba que el nombre sea valido y realiza una busqueda de metadatos en la base vectorial
     *aplicando una expresion de filtrado para asegurar que no estemos duplicando un ID ('sid') existente.
     *Se eligio el vector store como unica coleccion para evitar duplicidad de datos.
     *Aprovechamos que MongoDB Atlas permite filtrar por metadatos usando filterExpression,
     *que actua como una consulta exacta sobre los campos indexados.
     *La alternativa habria sido mantener dos colecciones separadas, lo que introduce redundancia.
     *</p>
     *@param game El objeto juego que queremos añadir.
     *@throws IllegalArgumentException Si el nombre esta vacio o si el ID del juego ya esta registrado.
     */
    public void insertarGame (Game game) {
        if (game.name == null || game.name.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }

        //Comprobamos si el juego ya existe usando un filtro directo por metadatos (sid)
        List<Document> existe = vectorStore.similaritySearch
                (SearchRequest.builder()
                .query("") //Consulta vacia porque solo queremos forzar el filtro exacto
                .topK(1)
                .filterExpression("sid == " + game.sid)
                .build());

        if (existe != null && !existe.isEmpty()) {
            throw new IllegalArgumentException("El ID " + game.sid + " ya existe.");
        }
        //Si esta bien, lo pasamos a documento y lo añadimos a MongoDB Atlas
        vectorStore.add(List.of(game2document(game)));
    }

    /**
     *Realiza una busqueda semantica de videojuegos segun la consulta del usuario.
     *<p>
     *Spring AI se encarga de convertir la frase del usuario a un vector, y pide a MongoDB Atlas
     *que compare la distancia coseno entre ese vector y el de los juegos que tenemos guardados,
     *devolviendo los 5 resultados mas cercanos.
     *</p>
     *@param query Frase descriptiva escrita por el usuario en lenguaje natural.
     *@return Lista con los 5 documentos de videojuegos con mayor coincidencia.
     */
    public List<Document> recomendar(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5) //Le pedimos las 5 mejores coincidencias
                        .build()
        );
    }

    /**
     *Busca un videojuego por su ID (sid) y lo reconstruye a un objeto Game completo.
     *<p>
     *Dado que en nuestro diseño la base vectorial contiene toda la información en los metadatos,
     *este metdo busca el documento filtrando por el 'sid' y deserializa a mano las propiedades
     *para reconstruir el objeto de dominio original mediante el patron Builder.
     *</p>
     *@param sid El identificador unico de negocio que queremos localizar.
     *@return El objeto {@link Game} reconstruido con sus datos, o null si no se encuentra en el indice.
     */
    public Game buscarPorSid(int sid) {
        //Lanzamos una busqueda vacia con un filtro especifico por metadatos
        List<Document> resultado = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query("") //Consulta vacia, solo filtramos
                        .topK(1) //Solo nos interesa el elemento exacto
                        .filterExpression("sid == " + sid)
                        .build()
        );

        //Si no encuentra nada, devolvemos null
        if (resultado == null || resultado.isEmpty()) {
            return null;
        }

        //Recuperamos el documento y su mapa de metadatos
        Document doc = resultado.get(0);
        Map<String, Object> meta = doc.getMetadata();

        //Reconstruimos la entidad Game usando su patron Builder
        return Game.builder()
                .sid(sid)
                .name((String) meta.getOrDefault("name", ""))
                .store_url((String) meta.getOrDefault("store_url", ""))
                .store_uscore((Integer) meta.getOrDefault("store_uscore", 0))
                .full_price((Integer) meta.getOrDefault("full_price", 0))
                .description((String) meta.getOrDefault("description", ""))
                .developers((List<String>) meta.getOrDefault("developers", List.of()))
                .genres((List<String>) meta.getOrDefault("genres", List.of()))
                .tags((List<String>) meta.getOrDefault("tags", List.of()))
                .build();
    }
}
