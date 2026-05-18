package org.tortitas.tfg.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 *Modelo de datos que representa a un videojuego dentro de la aplicacion.
 *<p>
 *Esta clase sirve para volcar el catalogo desde el archivo JSON a objetos de Java
 *y para organizar los metadatos de los juegos que luego guardamos en MongoDB Atlas.
 *</p>
 *@author Prabhnoor Singh Kaur
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@JsonIgnoreProperties(ignoreUnknown = true)//Si el archivo JSON trae campos de mas que no usamos aqui, Jackson los ignora
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Game {
    /**Identificador unico del videojuego (ID de la tienda).*/
    @Id
    public int sid;

    /**Enlace directo a la tienda donde esta publicado el juego.*/
    public String store_url;

    /**Puntuacion media otorgada por los usuarios en la tienda.*/
    public Integer store_uscore;

    /**Informacion o fecha de publicacion en la plataforma.*/
    public String published_store;

    /**Titulo oficial del videojuego.*/
    public String name;

    /**Descripcion del juego, utilizada por la IA para la busqueda semantica.*/
    public String description;

    /**Precio total del juego en la tienda.*/
    public Integer full_price;

    /**Lista de desarrolladores o estudios encargados del juego.*/
    public List<String> developers;

    /**Idiomas en los que se encuentra disponible el juego.*/
    public List<String> languages;

    /**Generos principales del juego.*/
    public List<String> genres;

    /**Etiquetas descriptivas asignadas por la comunidad o la tienda.*/
    public List<String> tags;
}