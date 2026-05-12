package org.tortitas.tfg.controllers;

import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.GameService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test/admin")
    public String admin() {
        return "Admin content";
    }

    //Los PreAuthorize son como si le estes diciendo a Spring que mire antes que rol tiene el que está intentando acceder
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/recommendations")
    public ResponseEntity<?> recomendar(
            @RequestParam String product) {

        List<String> recomendaciones = gameService.recomendar(product);
        return ResponseEntity.ok(recomendaciones);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/products")
    public ResponseEntity<?> insertarProducto(
            @RequestBody Game juego) {

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