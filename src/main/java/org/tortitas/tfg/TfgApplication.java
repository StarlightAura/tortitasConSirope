package org.tortitas.tfg;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.controllers.EmbeddingController;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.GameService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableMongoRepositories
public class TfgApplication {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private GameService gameService;

    public static void main(String[] args) {
        SpringApplication.run(TfgApplication.class, args);
    }
    @Bean
    public CommandLineRunner cargarDatos() {
        return args -> {
            long count = gameRepository.count();
            if (count == 0) {
                gameService.cargarJuegosDesdeJson("src/main/resources/out.json");
            } else {
                System.out.println("Juegos ya cargados en MongoDB (" + count + "), saltando carga.");
            }
        };
    }
}

