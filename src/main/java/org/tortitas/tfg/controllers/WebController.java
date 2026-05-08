package org.tortitas.tfg.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.exception.AuthException;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.WebService;

import java.util.Vector;

@Controller
@AllArgsConstructor
public class WebController {
    private JWTToken jwtToken;
    private GameRepository gameRepository;
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    private WebService webService;
    private AuthException authException;
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

    @GetMapping("/web/api/test")
    public ResponseEntity<String> ResponseEntity(){
        return ResponseEntity.status(HttpStatus.OK)
                .body("Correcto! Tienes acceso al web controller");
    }

    @PostMapping("/web/signup")
    public String signup(@RequestParam String nombreUser, @RequestParam String password, Model model) {
        try {
            final UserRequestDTO dto = new UserRequestDTO(nombreUser, password);
            webService.signup(dto);
            model.addAttribute(
                    "success",
                    "Usuario registrado. Ahora inicia sesión.");
            return "login";
        }
        catch (Exception e) {
            authException.handle(model,e);
            return "login";

        }

    }

    //===========================================================================================================

    @PostMapping("/web/signin")
    public String signin(@RequestParam String nombreUser, @RequestParam String password, HttpSession session, Model model) {
        try {
            final UserRequestDTO dto = new UserRequestDTO(nombreUser, password);
            webService.signin(dto, session);
            return "redirect:/home";
        } catch (Exception e) {
            authException.handle(model,e);
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
    public String recomendar(@RequestParam String product,
                             HttpSession session,
                             Model model) {
        try{
            model = webService.recommendation(product,session,model);
            return "home";
        } catch (Exception e) {
            authException.handle(model,e);
            session.invalidate();
            return "redirect:/login";
        }
    }

    //===========================================================================================================

    //TODO solo pueden insertar los admin
    /*Cambios:
     * 1) Lo primero seria que en vez de poner los maravillosos doscientos parametros sueltos, lo he reducido a tres
     * principalmente porque si decidieramos cambiar añadiendo o quitando algo de Game ya seria un problema (aunque espero no cambiar nada de esa clase)
     * ademas de que siendo sinceros quedaba feo de collons
     * 2) Comprueba el token antes de nada, eso no ha cambiado
     * 3) Ahora si un cambio, ver si tiene el permiso para insertar, si no lo tiene y de algun modo lo intenta, le hace bullying con pasivo agresividad
     * 4) Lo ultimo seria que ahora ademas de pasarle el username, tambien le pasamos el rol. Antes nos daba igual por asi decirlo porque aunque con
     * cada peticion se vaciara el model, al haber solo un tipo de usuario no habia conflicto digamos, pero ahora si seria un problema.
     * */
    @PostMapping("/web/products")
    public String insertarJuego(Game juego, HttpSession session, Model model) {
        if (session.getAttribute("token") == null) return "redirect:/login"; //comprueba sesion y valida el token

        String token = (String) session.getAttribute("token");
        if (!jwtToken.isTokenValid(token)) {
            session.invalidate();
            return "redirect:/login";
        }

        String rol = (String) session.getAttribute("rol");
        if (!"ADMIN".equals(rol)) {
            model.addAttribute("errorRol", "¿Pero tú quién eres?");
            return "home";
        }

        try {
            if (gameRepository.existsById(juego.sid)) {
                model.addAttribute("errorInsert", "El ID " + juego.sid + " ya existe. ¡No podemos duplicar la realidad!");
            } else {
                float[] vector = ollamaEmbeddingModel.embed(juego.game2document()); //convierte el juego a texto para que ollama lo vectorice
                java.util.Vector<Double> embeddings = new java.util.Vector<>();
                for (float v : vector) embeddings.add((double) v); //conversion de float a vector
                juego.embeddings = embeddings; //se guarda el vector en el objeto jueguito
                gameRepository.save(juego); //y se inserta en la bbdd

                model.addAttribute("successInsert", "Juego \"" + juego.name + "\" insertado y vectorizado 🗿");
            }
        } catch (Exception e) {
            model.addAttribute("errorInsert", "Error al insertar: " + e.getMessage());
        }

        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", rol);
        return "home";
    }
}