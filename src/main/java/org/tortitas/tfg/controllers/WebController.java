package org.tortitas.tfg.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.dto.CreateUserDTO;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.repositories.UserRepository;
import org.tortitas.tfg.services.GameService;
import org.tortitas.tfg.services.UserService;

import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class WebController {

    private final GameService gameService;
    private final JWTToken jwtToken;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GameRepository gameRepository;
    private final OllamaEmbeddingModel ollamaEmbeddingModel;

    private final UserService userService;

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
        return "home";
    }

    //===========================================================================================================

    @PostMapping("/web/signup")
    public String signup(@RequestParam String nombreUser,
                         @RequestParam String password,
                         Model model) {

        try {
            CreateUserDTO dto = new CreateUserDTO(nombreUser, password);
            userService.create(dto);
            model.addAttribute("success", "Usuario registrado. Ahora inicia sesión.");
            return "login";
        } catch (IllegalStateException e) {
            model.addAttribute("Error", e.getMessage());
            return "login";
        }
    }

    //===========================================================================================================

    @PostMapping("/web/signin")
    public String signin(@RequestParam String nombreUser,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) throws JoseException {
        Optional<?> userOpt = userRepository.findByNombreUser(nombreUser); //busca un user que puede o no existir
        if (userOpt.isEmpty()) { //si no existe pa tu casa (login)
            model.addAttribute("error", "Usuario no encontrado");
            return "login";
        }
        User user =
                (User) userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) { //comprueba contraseña y lo mismo que con el user
            model.addAttribute("error", "Contraseña incorrecta");
            return "login";
        }
        String token = jwtToken.generateToken(nombreUser); //crea el jwt del usuario
        session.setAttribute("token", token); //se guarda el token
        session.setAttribute("username", nombreUser); //se guarda el nombre
        return "redirect:/home";
    }

    //===========================================================================================================

    @GetMapping("/web/logout")
    public String logout(HttpSession session) {
        session.invalidate(); //borra token y user
        return "redirect:/login"; //vuelves al login
    }

    //===========================================================================================================

    @GetMapping("/web/recommendations")
    public String recomendar(@RequestParam String product,
                             HttpSession session,
                             Model model) {
        if (session.getAttribute("token") == null) return "redirect:/login"; //si no hay sesion, pa tu casa

        String token = (String) session.getAttribute("token");
        if (!jwtToken.isTokenValid(token)) { //si el token no es valido, pa tu casa
            session.invalidate();
            return "redirect:/login";
        }

        List<String> recomendaciones = gameService.recomendar(product); //llama al service y nos da la lista de jueguitos
        model.addAttribute("recomendaciones", recomendaciones); //envia los datos al html
        model.addAttribute("query", product); //guarda la busqueda
        model.addAttribute("username", session.getAttribute("username"));
        return "home";
    }

    //===========================================================================================================

    //TODO solo pueden insertar los admin
    @PostMapping("/web/products") //no puse un if por si existe el juego
    public String insertarJuego(@RequestParam int sid,
                                @RequestParam String name,
                                @RequestParam(required = false, defaultValue = "") String description,
                                @RequestParam(required = false, defaultValue = "") String genres,
                                @RequestParam(required = false, defaultValue = "") String tags,
                                @RequestParam(required = false, defaultValue = "") String developers,
                                @RequestParam(required = false, defaultValue = "") String published_store,
                                @RequestParam(required = false, defaultValue = "0") int full_price,
                                @RequestParam(required = false, defaultValue = "0") int store_uscore,
                                @RequestParam(required = false, defaultValue = "") String languages,
                                @RequestParam(required = false, defaultValue = "") String store_url,
                                HttpSession session,
                                Model model) {
        if (session.getAttribute("token") == null) return "redirect:/login"; //comprueba sesion y valida el token

        String token = (String) session.getAttribute("token");
        if (!jwtToken.isTokenValid(token)) {
            session.invalidate();
            return "redirect:/login";
        }

        try {
            Game juego = new Game();
            juego.sid = sid;
            juego.name = name;
            juego.description = description;
            juego.genres = genres;
            juego.tags = tags;
            juego.developers = developers;
            juego.published_store = published_store;
            juego.full_price = full_price;
            juego.store_uscore = store_uscore;
            juego.languages = languages;
            juego.store_url = store_url;

            float[] vector = ollamaEmbeddingModel.embed(juego.game2document()); //convierte el juego a texto para que ollama lo vectorice
            java.util.Vector<Double> embeddings = new java.util.Vector<>();
            for (float v : vector) embeddings.add((double) v); //conversion de float a vector
            juego.embeddings = embeddings; //se guarda el vector en el objeto jueguito
            gameRepository.save(juego); //y se inserta en la bbdd

            model.addAttribute("successInsert", "Juego \"" + name + "\" insertado y vectorizado ✅");
        } catch (Exception e) {
            model.addAttribute("errorInsert", "Error al insertar: " + e.getMessage());
        }

        model.addAttribute("username", session.getAttribute("username"));
        return "home";
    }
}



/*
*
* Versión anterior del singUp
*
*
*
* */