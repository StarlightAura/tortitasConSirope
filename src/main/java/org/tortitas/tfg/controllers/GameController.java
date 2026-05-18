package org.tortitas.tfg.controllers;

import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.services.GameService;
import java.util.List;

/**
 *Controlador REST encargado de gestionar los endpoints del catalogo de videojuegos y la IA.
 *<p>
 *En este componente controlamos las peticiones que tienen que ver con los juegos.
 *Ofrece un endpoint publico para pedir recomendaciones semanticas a la base de datos
 *vectorial y otro privado para que el administrador pueda meter juegos nuevos al sistema.
 *</p>
 *@author Prabhnoor Singh Kaur
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@RestController
@RequestMapping("/api")
public class GameController {
    @Autowired private GameService gameService;

    /**
     *Endpoint para obtener recomendaciones de juegos usando IA.
     *<p>
     *En lugar de hacer una busqueda de texto exacta , le pasamos la frase en lenguaje natural del usuario
     *al servicio para que busque juegos por similitud conceptual en el Vector Store.
     *</p>
     *@param product La frase o descripcion que introduce el usuario.
     *@return Una lista de objetos {@link Document} con los juegos mas parecidos que ha encontrado la IA.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> recomendar(@RequestParam String product) {
        //Llamamos al servicio para buscar por proximidad de embeddings
        List<Document> recomendaciones = gameService.recomendar(product);
        //Devolvemos la lista de documentos en formato JSON
        return ResponseEntity.ok(recomendaciones);
    }

    /**
     *Endpoint para la insercion y vectorización de un nuevo videojuego en el catalogo de juegos.
     *<p>
     *Ruta protegida por rol, que recibe los datos de un videojuego en formato JSON, gestiona su
     *almacenamiento en la base de datos documental de MongoDB y genera su embedding para
     *integrarlo inmediatamente en el almacenamiento vectorial de Atlas.
     *</p>
     *@param juego Objeto que contiene la información del videojuego a registrar.
     *@return Mensaje de exito si se procesa correctamente, una respuesta HTTP 400 si el identificador (sid)
     *ya existe, o una respuesta HTTP 500 ante fallos del servidor de MongoDB o del modelo de IA.
     */
    @PostMapping("/products")
    public ResponseEntity<?> insertarProducto(@RequestBody Game juego) {
        try {
            gameService.insertarGame(juego);
            return ResponseEntity.ok("Producto insertado y vectorizado correctamente");
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error al insertar: " + e.getMessage());
        }
    }
}