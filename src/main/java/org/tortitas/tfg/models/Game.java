package org.tortitas.tfg.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

public class Game {
    public int sid;
    public String store_url;
    public int store_uscore;
    public String published_store;
    public String name;
    public String description;
    public int full_price;
    public String developers;
    public String languanges;
    public String genres;
    public String tags;
}
