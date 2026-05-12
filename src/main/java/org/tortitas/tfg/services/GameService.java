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

    //Nuevo metodo privado que sirve para vectorizar (como bien indica el nombre) cualquier texto
    // y devolver el embedding. Es lo mismo que teniamos repetido en GameService, GameController y
    // WebController. Ahora simplemente esta una unica vez escrito aqui y el resto que lo llame

    private Vector<Double> vectorizar (String texto) {
        float[] vector = ollamaEmbeddingModel.embed(texto);
        Vector<Double> embeddings = new Vector<>();
        for (float v : vector) embeddings.add((double) v);
        return embeddings;
    }


    public Vector<Double> vectorizarGame(Game game) {
        return vectorizar(game.game2document().getFormattedContent());
    }

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
                juego.embeddings = vectorizarGame(juego);
            }
            gameRepository.save(juego);
            System.out.println("Guardado: " + juego.name);
        }
    }

    //Esto antes se hacia tanto en el Game controller como en el WebController, asi que se mueve aqui
    //y solo tenemos que llamarlo
    public void insertarGame (Game game) {
        if (gameRepository.existsById(game.sid)) {
            throw new IllegalArgumentException("El ID " + game.sid + " ya existe.");
        }
        game.embeddings = vectorizarGame(game);
        gameRepository.save(game);
    }

    // Esto es lo que llama el bot con la query del usuario
    public List<AbstractMap.SimpleEntry<Game, Double>> recomendar(String query)   {
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
    }
}
