package org.tortitas.tfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.services.GameService;
import java.util.AbstractMap;
import java.util.List;

@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired private GameService gameService;
    @Autowired private JWTToken jwtToken;

    //=================================================================================================================
    /*@GetMapping("/recommendations")
    public ResponseEntity<?> recomendar(
            @RequestParam String product,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        ResponseEntity<?> authError = validarToken(authHeader);
        if (authError != null) return authError;

        List<AbstractMap.SimpleEntry<Game, Double>> recomendaciones = gameService.recomendar(product);
        return ResponseEntity.ok(recomendaciones);
    }*/

    //=================================================================================================================

    @PostMapping("/products")
    public ResponseEntity<?> insertarProducto(
            @RequestBody Game juego,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        ResponseEntity<?> authError = validarToken(authHeader);
        if (authError != null) return authError;

        try {
            gameService.insertarGame(juego);
            return ResponseEntity.ok("Producto insertado y vectorizado correctamente");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al insertar: " + e.getMessage());
        }
    }

    //=================================================================================================================

    //Nuevo metodo privado para eviatr que se repita en todos los endpoints (antes lo teniamos asi)
    //Hace lo mismo, solo hay que llamarlo y nos olvidamos de repetir codigo
    private ResponseEntity<?> validarToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token requerido");
        }
        if (!jwtToken.isTokenValid(authHeader.substring(7))) {
            return ResponseEntity.status(401).body("Token inválido");
        }
        return null;
    }

}