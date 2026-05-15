package org.tortitas.tfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.services.UserService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        ResponseEntity<?> response;
        try {
            userService.registrarUser(body.get("nombreUser"), body.get("password"));
            response = ResponseEntity.ok("Usuario registrado correctamente");
        } catch (RuntimeException e) {
            response = ResponseEntity.badRequest().body(e.getMessage());
        }
        return response;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> creds){
        ResponseEntity<?> response;
        try {
            String token = userService.verificarSignin(creds.get("nombreUser"), creds.get("password"));
            response = ResponseEntity.ok(Map.of("token", token));
        }catch (Exception e) {
            response = ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        return response;
    }
}