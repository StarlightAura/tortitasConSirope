package org.tortitas.tfg.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)// con esto ignora campos que no estan en el .json
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

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
}