package org.tortitas.tfg.controllers;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.GameService;

import java.util.AbstractMap;
import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired
    private GameService gameService;
    @Autowired private JWTToken jwtToken;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;
    @GetMapping("/recommendations")
    public ResponseEntity<?> recomendar(
            @RequestParam String product,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token requerido");
        }

        String token = authHeader.substring(7);
        if (!jwtToken.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token inválido o expirado");
        }

        List<AbstractMap.SimpleEntry<Game, Double>> recomendaciones = gameService.recomendar(product);
        return ResponseEntity.ok(recomendaciones);
    }
    @PostMapping("/products")
    public ResponseEntity<?> insertarProducto(
            @RequestBody Game juego,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token requerido");
        }
        if (!jwtToken.isTokenValid(authHeader.substring(7))) {
            return ResponseEntity.status(401).body("Token inválido");
        }

        try {
            float[] vector = ollamaEmbeddingModel.embed(juego.game2document());
            Vector<Double> embeddings = new Vector<>();
            for (float v : vector) embeddings.add((double) v);
            juego.embeddings = embeddings;
            gameRepository.save(juego);
            return ResponseEntity.ok("Producto insertado y vectorizado correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al vectorizar: " + e.getMessage());
        }
    }
}