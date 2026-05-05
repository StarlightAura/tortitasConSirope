package org.tortitas.tfg.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Vector;

@JsonIgnoreProperties(ignoreUnknown = true)// con esto ignora campos que no estan en el .json
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

@Document("GameItem")
public class Game {
    @Id
    public int sid;
    public String store_url;
    public int store_uscore;
    public String published_store;
    public String name;
    public String description;
    public int full_price;
    public String developers;
    public String languages;
    public String genres;
    public String tags;
    public Vector<Double> embeddings; //Para guardar los vectores que genera nuestra amiga Ollama

    // Si lo pongo sin el org me chilla por el document de mongo
    public org.springframework.ai.document.Document game2document(){

        /*Si le pasamos tooodo ollama se puede volver crazy porque necesita contexto
        para crear los vectores o embeddings o como se diga, y resulta y acontece que por lo que sea
        saber el precio del jueguito le importa entre poco y nada por ejemplo
        */
        String juegosContent = String.format(
                /*Por alguna razón, la ia en general, no entiende el contexto de algunas cosas si no
                 * vas con ella de la manita, me explico, el nombre y la descripcion lo entiende perfe,
                 * sin embargo, si no le dices explicitamente con etiquetas que son el resto de cosas
                 * se vuelve tonta y puede pensar que el genero terror es el nombre de el desarrollador por ejemplo
                 * y en resumen liarla parda*/
                "%s. %s. Genres: %s. Tags: %s. Developers: %s",
                this.name, this.description, this.genres, this.tags, this.developers
        );
        return new org.springframework.ai.document.Document(juegosContent);
    }
}