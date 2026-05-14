package org.tortitas.tfg;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.GameService;

import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class TfgApplication {
    @Autowired
    GameRepository gameRepository;
    @Autowired
    private GameService gameService;
    @Autowired
    private VectorStore vectorStore;

    public static void main(String[] args) {
        SpringApplication.run(TfgApplication.class, args);
    }
    /*@Bean
    public CommandLineRunner cargarDatos() {
        return args -> {
            gameService.cargarJuegosDesdeJson("src/main/resources/out.json");
        };
    }*/
}