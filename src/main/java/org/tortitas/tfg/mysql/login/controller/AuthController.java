package org.tortitas.tfg.mysql.login.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.mysql.login.service.AuthService;


@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> register (@Valid @RequestBody final RegisterRequest request){
        final TokenResponse token = service.register(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> authenticate(@RequestBody final LoginRequest request){
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public TokenResponse refreshToken (@RequestHeader(HttpHeaders.AUTHORIZATION) final String authHeader){
        return service.refreshToken(authHeader);
    }


}
