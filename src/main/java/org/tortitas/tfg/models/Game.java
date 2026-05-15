package org.tortitas.tfg.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
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
    public List<String> developers;
    public List<String> languages;
    public List<String> genres;
    public List<String> tags;
    //public Vector<Double> embeddings;

    /*public org.springframework.ai.document.Document game2document(){
        String juegosContent = String.format(
                "%s. %s. Genres: %s. Tags: %s. Developers: %s",
                this.name, this.description, this.genres, this.tags, this.developers
        );
        return new org.springframework.ai.document.Document(juegosContent);
    }*/
}