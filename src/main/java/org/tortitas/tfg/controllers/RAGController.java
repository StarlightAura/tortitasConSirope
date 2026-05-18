package org.tortitas.tfg.controllers;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tortitas.tfg.services.GameService;

/**
 *Controlador REST para hacer pruebas y consultas directas con el motor de IA (RAG).
 *<p>
 *Este componente acua como un endpoint tecnico de desarrollo. Nos permite interactuar
 *directamente con el Vector Store de MongoDB Atlas y ver como el modelo de Ollama
 *transforma los textos de los juegos en embeddings (es decir, arrays de numeros tipo float).
 *</p>
 * @author StarlightAura
 */
@RestController
@RequestMapping("/ai")
public class RAGController {
    @Autowired private MongoDBAtlasVectorStore vectorStore;
    @Autowired private GameService gameService;

    /**
     *Endpoint de desarrollo para generar y visualizar el embedding de un documento.
     *<p>
     *Le pasamos un objeto Document de Spring AI y, llamando al modelo de Ollama,
     *nos devuelve el vector numerico en bruto. Esto viene bien para comprobar que la IA
     *responde correctamente y ver la dimension del vector en la consola o en Postman.
     *</p>
     *@param document El objeto documento que contiene el texto que queremos vectorizar.
     *@return Un array de floats que representa el vector matematico del texto.
     */
    @GetMapping("/embedding")
    public float[] embed(Document document){
        //Le pedimos al servicio que procese el documento con Ollama y nos devuelva sus numeros
        return gameService.obtenerEmbedding(document);
    }
}
