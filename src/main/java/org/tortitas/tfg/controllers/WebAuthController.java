package org.tortitas.tfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepo;

@Controller
@RequestMapping("/auth")
public class WebAuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String vistaLogin() {
        return "login-page"; // templates/login-page.html
    }

    @GetMapping("/registro")
    public String vistaRegistro() {
        return "registro"; // templates/registro.html
    }

    @PostMapping("/registro")
    public String procesoRegistro(@RequestParam String username, @RequestParam String password) {
        User nuevo = new User();
        nuevo.setNombreUser(username);
        nuevo.setPassword(passwordEncoder.encode(password)); // Ciframos la clave

        // Si es el primero, lo hacemos ADMIN
       /* if (userRepo.count() == 0) {
            nuevo.setRol("ROL_ADMIN");
        } else {
            nuevo.setRol("ROL_USER");
        }*/

        userRepo.save(nuevo);
        return "redirect:/auth/login";
    }


}