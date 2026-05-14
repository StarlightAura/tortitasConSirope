package org.tortitas.tfg.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.models.Rol;
import org.tortitas.tfg.services.GameService;
import org.tortitas.tfg.services.UserService;
import java.util.AbstractMap;
import java.util.List;

@Controller
public class WebController {
    @Autowired private GameService gameService;
    @Autowired private UserService userService;
    @Autowired private JWTToken jwtToken;

    //===========================================================================================================

    @GetMapping("/")
    public String index(HttpSession session) {
        // Si la sesion esta iniciada va directo al home
        if (session.getAttribute("token") != null) return "redirect:/home";
        return "redirect:/login"; //sino te logeas
    }

    //===========================================================================================================

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("token") != null) return "redirect:/home"; //si ya estas logeado pues al home
        return "login"; //sino te logeas
    }

    //===========================================================================================================

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        if (session.getAttribute("token") == null) return "redirect:/login"; //mas de lo mismo, sin logearte no entras

        model.addAttribute("username", session.getAttribute("username")); //se pasa el username al thymeleaf
        model.addAttribute("rol", session.getAttribute("rol")); // y ahora tambien el rol que tiene el querido usuario
        return "home";
    }

    //===========================================================================================================

   @PostMapping("/web/signup")
    public String signup(@RequestParam String nombreUser, @RequestParam String password, Model model) {
        try {
            userService.registrarUser(nombreUser, password);
            model.addAttribute("success", "Usuario registrado. Ahora inicia sesión");
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "login";
    }

    //===========================================================================================================

    @PostMapping("/web/signin")
    public String signin(@RequestParam String nombreUser, @RequestParam String password, HttpSession session, Model model){

        try {
            String token = userService.verificarSignin(nombreUser, password);
            Rol rol = userService.obtenerRol(nombreUser);
            session.setAttribute("token", token); //se guarda el token
            session.setAttribute("username", nombreUser); //se guarda el nombre
            session.setAttribute("rol", rol.name()); // y guardamos el tipo de rol tambien
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login";
        }
    }

    //===========================================================================================================

    @GetMapping("/web/logout")
    public String logout(HttpSession session) {
        session.invalidate(); //borra token y user
        return "redirect:/login"; //vuelves al login
    }

    //===========================================================================================================

    @GetMapping("/web/recommendations")
    public String recomendar(@RequestParam String product, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null || !jwtToken.isTokenValid(token)) {
            session.invalidate();
            return "redirect:/login";
        }

        List<Document> recomendaciones = gameService.recomendar(product);
        model.addAttribute("recomendaciones", recomendaciones); //envia los datos al html
        model.addAttribute("query", product); //guarda la busqueda
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol")); //para mostrar una interfaz u otra dependiendo del rol
        return "home";
    }

    //===========================================================================================================

    @PostMapping("/web/products")
    public String insertarJuego(Game juego, HttpSession session, Model model) {
        String token = (String) session.getAttribute("token");
        if (token == null || !jwtToken.isTokenValid(token)) {
            return "redirect:/login";
        }

        String rol = (String) session.getAttribute("rol");
        if (!"ADMIN".equals(rol)) {
            model.addAttribute("errorRol", "¿Pero tú quién eres?");
            return "home";
        }

        try {

            gameService.insertarGame(juego);
            model.addAttribute("successInsert", "Juego \"" + juego.name + "\" insertado correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorInsert", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("errorInsert", "Error al insertar");
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", rol);
        return "home";
    }
}