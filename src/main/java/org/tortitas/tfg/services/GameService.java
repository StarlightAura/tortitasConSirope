package org.tortitas.tfg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.tortitas.tfg.models.Game;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    @Autowired
    private MongoDBAtlasVectorStore vectorStore;

    public Document game2document(Game game){

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

        return new Document(game.name, metadata);
    }

    // Llama a esto UNA VEZ para cargar el JSON en MongoDB y generar embeddings
    public void cargarJuegosDesdeJson(String rutaJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Game> juegos = mapper.readValue(
                new File(rutaJson),
                new TypeReference<List<Game>>() {}
        );
        System.out.println("Juegos leídos del JSON: " + juegos.size());


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

    }

    public List<Document> recomendar(String query) {
        return vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(5)
                        .build()
        );
    }
}
