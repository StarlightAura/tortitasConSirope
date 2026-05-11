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
import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.Rol;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.Map;
import java.util.Optional;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/test")
    public ResponseEntity<?>test(){
        return ResponseEntity.status(HttpStatus.OK).body("Entraste!");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserRequestDTO dto) {

        if (userRepository.findByNombreUser(dto.getName()).isPresent()) {
            return ResponseEntity.badRequest().body("Usuario ya existe");
        }

        User user = User.builder()
                .nombreUser(dto.getName())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Rol.ADMIN)
                .build();

        userRepository.save(user);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @Autowired private JWTToken jwtToken;

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> creds) throws JoseException {

        String name = creds.get("nombreUser");
        String password = creds.get("password");

        authenticationManager.authenticate(
             new UsernamePasswordAuthenticationToken(
                     name,
                     password
             )
        );

        Optional<User> user = userRepository.findByNombreUser(creds.get("nombreUser"));
        if (user.isEmpty() || !passwordEncoder.matches(creds.get("password"), user.get().getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        String token = jwtToken.generateToken(user.get().getNombreUser(), user.get().getRole());
        return ResponseEntity.ok(Map.of("token", token));
    }
}