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

    //El requestbody pilla el json que llega en la peticion y lo convierte a un map,
    // asi se pueden coger los valores
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        try {
            userService.registrarUser(body.get("nombreUser"), body.get("password"));
            return ResponseEntity.ok("Usuario registrado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Esto lo he dejado casi igual, ya que hace lo mismo que el de arriba, recibe el nombreuser y password
    //del cuerpo del json
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> creds){
        try {
            //se validan las creds y genera el jwt
            String token = userService.verificarSignin(creds.get("nombreUser"), creds.get("password"));
            return ResponseEntity.ok(Map.of("token", token));
        }catch (Exception e) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }
}