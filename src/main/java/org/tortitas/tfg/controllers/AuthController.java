package org.tortitas.tfg.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.dto.UserRequestWebDTO;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.models.Rol;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.record.LoginRequest;
import org.tortitas.tfg.record.RegisterRequest;
import org.tortitas.tfg.record.TokenResponse;
import org.tortitas.tfg.repositories.UserRepository;
import org.tortitas.tfg.services.AuthService;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @GetMapping("/test")
    public ResponseEntity<?>test(){
        return ResponseEntity.status(HttpStatus.OK).body("Test de conexión");
    }

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody RegisterRequest request) throws JoseException {
        final TokenResponse tokenResponse = service.register(request);

        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);

    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody final LoginRequest request) throws JoseException {
        final TokenResponse tokenResponse = service.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
    }
}