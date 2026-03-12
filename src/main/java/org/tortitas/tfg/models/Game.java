package org.tortitas.tfg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
