package org.tortitas.tfg.controllers;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByNombreUser(user.getNombreUser()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuario ya existe");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @Autowired private JWTToken jwtToken;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> creds) throws JoseException {
        Optional<User> user = userRepository.findByNombreUser(creds.get("nombreUser"));
        if (user.isEmpty() || !passwordEncoder.matches(creds.get("password"), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        String token = jwtToken.generateToken(user.get().getNombreUser(), user.get().getRol());
        return ResponseEntity.ok(Map.of("token", token));
    }
}