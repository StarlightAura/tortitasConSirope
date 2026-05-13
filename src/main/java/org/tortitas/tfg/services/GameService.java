package org.tortitas.tfg.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.tortitas.tfg.CosineSimilarity;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.repositories.GameRepository;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    // Llama a esto UNA VEZ para cargar el JSON en MongoDB y generar embeddings
    public void cargarJuegosDesdeJson(String rutaJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<Game> juegos = mapper.readValue(
                new File(rutaJson),
                new TypeReference<List<Game>>() {}
        );

        for (Game juego : juegos) {
            // Solo vectoriza si no tiene embedding ya
            if (juego.embeddings == null || juego.embeddings.isEmpty()) {
                float[] vector = ollamaEmbeddingModel.embed(juego.game2document());
                Vector<Double> embeddings = new Vector<>();
                for (float v : vector) embeddings.add((double) v);
                juego.embeddings = embeddings;
            }
            gameRepository.save(juego);
            System.out.println("Guardado: " + juego.name);
        }
    }

    // Esto es lo que llama el bot con la query del usuario
    public List<AbstractMap.SimpleEntry<Game, Double>> recomendar(String query)   {
        // 1. Vectorizar la query
        float[] vectorQuery = ollamaEmbeddingModel.embed(query);
        Vector<Double> queryEmbedding = new Vector<>();
        for (float v : vectorQuery) queryEmbedding.add((double) v);

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

       /*  // Esto es lo que llama el bot con la query del usuario
        public List<String> recomendar(String query) {
        // 1. Vectorizar la query
        float[] vectorQuery = ollamaEmbeddingModel.embed(query);
        Vector<Double> queryEmbedding = new Vector<>();
        for (float v : vectorQuery) queryEmbedding.add((double) v);

        // 2. Comparar con todos los juegos en MongoDB
        List<Game> todos = gameRepository.findAll();

        return todos.stream()
                .filter(g -> g.embeddings != null && !g.embeddings.isEmpty())
                .map(g -> new AbstractMap.SimpleEntry<>(
                        g.name,
                        CosineSimilarity.cosineSimilarity(queryEmbedding, g.embeddings)
                ))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())) // mayor similitud primero
                .limit(5)
                //TODO QUITAR LOS PORCENTAJES MAYBE
                .map(e -> String.format("🎮 %s (%.0f%% similitud)", e.getKey(), e.getValue() * 100))
                .collect(Collectors.toList());
    }*/
    }
}
