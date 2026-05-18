package org.tortitas.tfg.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.services.GameService;
import org.tortitas.tfg.services.UserService;

/**
 *Controlador que gestiona la navegacion web y las vistas de la aplicacion.
 *<p>
 *Se encarga de dirigir al usuario entre las pantallas de login y home, procesar
 *los formularios de la interfaz y enviar los datos necesarios a las plantillas de Thymeleaf.
 *</p>
 *@author Laura Martín Martínez
 */
@Controller
public class WebController {
    @Autowired private GameService gameService;
    @Autowired private UserService userService;

    /**
     *Añade los datos del usuario al modelo antes de cargar cualquier vista.
     *<p>
     *Si el usuario ha iniciado sesion, recupera su nombre y su rol para que
     *Thymeleaf pueda personalizar la interfaz de forma dinamica.
     *</p>
     *@param session Sesion HTTP actual.
     *@param model Modelo para pasar los datos a la vista.
     */
    @ModelAttribute
    public void addAttributes(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");
        if (username != null) {
            model.addAttribute("username", username);
            model.addAttribute("rol", session.getAttribute("rol"));
        }
    }

    /**
     *Muestra la pantalla inicial de inicio de sesion (Login).
     *Mapea tanto la raiz "/" como la URL "/login".
     * @return El nombre de la plantilla HTML "login".
     */
    @GetMapping({"/", "/login"})
    public String index() {
        return "login";
    }

    /**
     *Muestra la pagina principal de la aplicacion (Home) una vez logueado.
     * @return El nombre de la plantilla HTML "home".
     */
    @GetMapping("/home")
    public String homePage() {
        return "home";
    }


    /**
     *Procesa el formulario de registro de un nuevo usuario desde la web.
     *<p>
     *Intenta crear el usuario a traves del servicio. Si sale bien, avisa en la pantalla
     *de login para que inicie sesion. Si falla recarga el login mostrando el mensaje de error.
     *</p>
     *@param nombreUser Nombre introducido en el formulario.
     *@param password Contraseña introducida en el formulario.
     *@param model Modelo para gestionar los mensajes de la interfaz.
     *@return Devuelve a la vista de "login".
     */
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


    /**
     *Procesa el inicio de sesion de un usuario.
     *<p>
     *Si las credenciales son correctas, saca el token JWT, el usuario y su rol, y los guarda
     *en la sesion HTTP del servidor para recordar quien es durante la navegacion.
     *Luego redirige al home.
     * </p>
     *@param nombreUser Nombre introducido en el formulario.
     *@param password Contraseña introducida en el formulario.
     *@param session La sesion donde guardaremos los datos de autenticacion del usuario.
     *@param model Modelo para gestionar errores en la interfaz.
     *@return Redireccion a "/home" si tiene exito, o recarga "login" si falla.
     */
    @PostMapping("/web/signin")
    public String signin(@RequestParam String nombreUser, @RequestParam String password, HttpSession session, Model model) {
        try {
            UserService.SigninResult result = userService.verificarSignin(nombreUser, password);
            session.setAttribute("token", result.token());
            session.setAttribute("username", nombreUser);
            session.setAttribute("rol", result.rol());
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "Credenciales incorrectas.");
            return "login";
        }
    }


    /**
     *Cierra la sesion del usuario actual.
     *<p>
     *Invalida la sesion HTTP por completo, lo que borra del servidor el token JWT,
     *el nombre de usuario y el rol, y manda al usuario de vuelta al login.
     *</p>
     *@param session La sesion que vamos a destruir.
     *@return Una redireccion a la pantalla de "/login".
     */
    @GetMapping("/web/logout")
    public String logout(HttpSession session) {
        session.invalidate(); //borra token y user
        return "redirect:/login"; //vuelves al login
    }


    /**
     *Solicita recomendaciones al servicio de IA.
     *<p>
     *Pide los juegos sugeridos a la base de datos vectorial y recarga el home pasandole
     *la lista de recomendaciones encontradas y la frase original que puso el usuario.
     *</p>
     *@param product La descripcion o frase que el usuario ha escrito en el buscador.
     *@param session La sesion del usuario.
     *@param model Modelo para pasar la lista de juegos recomendados a Thymeleaf.
     *@return Recarga el "home" con la lista de recomendaciones.
     */
    @GetMapping("/web/recommendations")
    public String recomendar(@RequestParam String product, HttpSession session, Model model) {
        model.addAttribute("recomendaciones", gameService.recomendar(product));
        model.addAttribute("query", product);
        return "home";
    }


    /**
     *Procesa la insercion de un nuevo videojuego por parte de un administrador.
     *@param juego Objeto mapeado automaicamente con los datos del formulario.
     *@param session La sesión del usuario.
     *@param model Modelo para avisar si el juego se inserto bien o si hubo un error.
     *@return Recarga el "home" manteniendo los avisos de exito o error en los paneles de administracion.
     */
    @PostMapping("/web/products")
    public String insertarJuego(Game juego, HttpSession session, Model model) {
        try {
            gameService.insertarGame(juego);
            model.addAttribute("successInsert", "Juego \"" + juego.name + "\" insertado correctamente.");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorInsert", e.getMessage());
        }
        return "home";
    }

    /**
     *Busca un videojuego por su identificador unico (sid)
     *<p>
     *Si encuentra el juego, lo pasa al modelo para que el panel de administracion de Thymeleaf
     *pinte sus metadatos detallados. Si no lo encuentra, manda un mensaje de aviso.
     *</p>
     *@param sid Identificador del videojuego a buscar.
     *@param model Modelo para enviar el juego o el error a la interfaz.
     *@return Recarga el "home" mostrando los datos de la entidad en el panel del administrador.
     */
    @GetMapping("/web/products/search")
    public String buscarJuegoPorId(@RequestParam int sid, Model model) {
        Game juegoEncontrado = gameService.buscarPorSid(sid);
        if (juegoEncontrado != null) {
            model.addAttribute("juegoInspeccionado", juegoEncontrado);
        } else {
            model.addAttribute("errorBusqueda", "La entidad con ID " + sid + " no existe en el vacío.");
        }
        return "home";
    }
}