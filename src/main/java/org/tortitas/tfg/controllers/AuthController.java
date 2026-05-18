package org.tortitas.tfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.services.UserService;
import java.util.Map;

/**
 *Controlador REST para gestionar el registro y el inicio de sesion de los usuarios.
 *<p>
 *Expone los endpoints de la API dedicados a la autenticacion.
 *Recibe los datos que envian los usuarios en formato JSON, los procesa a traves del
 *servicio UserService y devuelve las respuestas correspondientes (como el token de sesion).
 *</p>
 *@author Prabhnoor Singh Kaur
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private UserService userService;

    /**
     *Endpoint para registrar un nuevo usuario en la aplicacion.
     *<p>
     *Recibe los datos del JSON, llama al servicio para que encripte la contraseña
     *y guarde al usuario, y controla si el nombre de usuario ya esta cogido en la base de datos.
     *</p>
     *@param body Mapa que contiene las claves del JSON enviado: 'nombreUser' y 'password'.
     *@return Mensaje de exito si va bien (HTTP 200) o un aviso con el error si el usuario ya existe (HTTP 400).
     */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body) {
        ResponseEntity<?> response;
        try {
            //Le pasamos el nombre y la contraseña al servicio para que cree el usuario de forma segura
            userService.registrarUser(body.get("nombreUser"), body.get("password"));
            response = ResponseEntity.ok("Usuario registrado correctamente");
        } catch (RuntimeException e) {
            response = ResponseEntity.badRequest().body(e.getMessage());
        }
        return response;
    }

    /**
     *Endpoint para el inicio de sesion de los usuarios.
     *<p>
     *Comprueba si el usuario existe y si la contraseña coincide. Si esta en orden,
     *genera un token JWT y se lo devuelve al cliente para que pueda autenticarse en las siguientes peticiones.
     *</p>
     *@param creds Mapa con las credenciales que ha metido el usuario en la pantalla de login.
     *@return Un mapa JSON con el 'token' generado (HTTP 200) o un mensaje de error generico si fallan los datos (HTTP 401).
     */
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> creds){
        ResponseEntity<?> response;
        try {
            //Llamamos al servicio para verificar las credenciales. Si coinciden, nos devuelve un objeto con el JWT
            UserService.SigninResult result = userService.verificarSignin(
                    creds.get("nombreUser"), creds.get("password")
            );
            response = ResponseEntity.ok(Map.of("token", result.token()));
        }catch (Exception e) {
            //El mensaje es generico para evitar dar pistas de si lo que ha fallado es el nombre de usuario o la contraseña.
            response = ResponseEntity.status(401).body("Credenciales incorrectas");
        }
        return response;
    }
}