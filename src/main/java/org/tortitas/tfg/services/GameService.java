package org.tortitas.tfg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.tortitas.tfg.CosineSimilarity;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.repositories.GameRepository;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {
   // @Autowired
   // private GameRepository gameRepository;
    @Autowired
    private VectorStore vectorStore;


    public Document game2document(Game game){
        String juegosContent = String.format(
                "Nombre: %s. Descripción: %s. Géneros: %s. Tags: %s. Desarrolladores: %s. Puntuación: %d. Precio: %d",
                game.name, game.description, game.genres, game.tags, game.developers, game.store_uscore, game.full_price
        );

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("sid", game.sid);
        metadata.put("name", game.name != null ? game.name : "");
        metadata.put("store_url", game.store_url != null ? game.store_url : "");
        metadata.put("genres", game.genres != null ? game.genres : List.of());
        metadata.put("tags", game.tags != null ? game.tags : List.of());
        metadata.put("store_uscore",game.store_uscore);
        metadata.put("full_price", game.full_price);
        metadata.put("description", game.description != null ? game.description : "");
        metadata.put("developers", game.developers != null ? game.developers : List.of());

        return new Document(juegosContent, metadata);
        /*return new Document(juegosContent, Map.of(
                "sid", game.sid,
                "name", game.name !=null ? game.name: "",
                "store_url", game.store_url !=null ? game.store_url: "",
                "genres", game.genres !=null ? game.genres: "",
                "tags", game.tags !=null ? game.tags: "",
                "store_uscore", game.store_uscore,
                "full_price", game.full_price
        ));*/
    }

    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    //Nuevo metodo privado que sirve para vectorizar (como bien indica el nombre) cualquier texto
    // y devolver el embedding. Es lo mismo que teniamos repetido en GameService, GameController y
    // WebController. Ahora simplemente esta una unica vez escrito aqui y el resto que lo llame

    public Vector<Double> vectorizar(String texto) {
        float[] vector = ollamaEmbeddingModel.embed(texto);
        Vector<Double> embeddings = new Vector<>();
        for (float v : vector) embeddings.add((double) v);
        return embeddings;
    }


    /*public Vector<Double> vectorizarGame(Game game) {
        return vectorizar(game.game2document().getFormattedContent());
    }*/

    // Llama a esto UNA VEZ para cargar el JSON en MongoDB y generar embeddings
    public void cargarJuegosDesdeJson(String rutaJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Game> juegos = mapper.readValue(
                new File(rutaJson),
                new TypeReference<List<Game>>() {}
        );
        System.out.println("Juegos leídos del JSON: " + juegos.size());

        /*for (Game juego : juegos) {
            gameRepository.save(juego);
            System.out.println("Guardado: " + juego.name);
        }*/

        List<Document> docs = juegos.stream()
                .map(this::game2document)
                .collect(Collectors.toList());

        System.out.println("Total docs a insertar: " + docs.size());
        int batchSize = 5;
        for (int i = 0; i < docs.size(); i += batchSize) {
            List<Document> lote = docs.subList(i, Math.min(i + batchSize, docs.size()));
            try {
                vectorStore.add(lote);
                System.out.println("Insertados " + i + " - " + (i + lote.size()));
            } catch (Exception e) {
                System.out.println("ERROR " + i + ": " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }

    //Esto antes se hacia tanto en el Game controller como en el WebController, asi que se mueve aqui
    //y solo tenemos que llamarlo
    public void insertarGame (Game game) {

        List<Document> existe = vectorStore.similaritySearch(SearchRequest.builder()
                .query(game.name).topK(1).filterExpression("sid == " + game.sid).build());

        if (!existe.isEmpty()) {
            throw new IllegalArgumentException("El ID " + game.sid + " ya existe.");
        }
        vectorStore.add(List.of(game2document(game)));

       /* if (gameRepository.existsById(game.sid)) {
            throw new IllegalArgumentException("El ID " + game.sid + " ya existe.");
        }
        gameRepository.save(game);
        vectorStore.add(List.of(game2document(game)));*/
    }

    public List<Document> recomendar(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }

    /*public List<Game> buscarName(String name) {
        return gameRepository.findByName(name);
    }
    public void eliminarGames(int sid) {
        Game game = gameRepository.findById(sid).orElseThrow(() -> new IllegalArgumentException("El juego con ID " + sid + " no existe."));
        gameRepository.deleteById(sid);

        List<Document> doc = vectorStore.similaritySearch(SearchRequest.builder()
                .query(game.name)
                .topK(10)
                .build());
        doc.stream()
                .filter(d -> d.getMetadata().get("sid") != null && d.getMetadata().get("sid").toString().equals(String.valueOf(sid)))
                .forEach(d -> vectorStore.delete(List.of(d.getId())));
    }

    // Esto es lo que llama el bot con la query del usuario
    /*public List<AbstractMap.SimpleEntry<Game, Double>> recomendar(String query)   {
        // 1. Vectorizar la query
        Vector<Double> queryEmbedding = vectorizar(query);
        // 2. Comparar con todos los juegos en MongoDB
        List<Game> todos = gameRepository.findAll();

        return todos.stream()
                .filter(g -> g.embeddings != null && !g.embeddings.isEmpty())
                .map(g -> new AbstractMap.SimpleEntry<>(
                        g, CosineSimilarity.cosineSimilarity(queryEmbedding, g.embeddings) * 100
                ))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }*/
}
