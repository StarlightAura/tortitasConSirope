package org.tortitas.tfg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.GameService;

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

