package org.tortitas.tfg;

import com.mongodb.client.MongoClients;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.services.GameService;

@SpringBootApplication
@EnableMongoRepositories
public class TfgApplication {
    @Autowired
    MongoDBAtlasVectorStore vectorStore;
    @Autowired
    GameService gameService;

    @Value("${spring.mongodb.uri}")
    String uri;

    public static void main(String[] args) {
        SpringApplication.run(TfgApplication.class, args);
    }
    @Bean
    public CommandLineRunner cargarDatos() {
        return args -> {
            long c = MongoClients.create(uri).getDatabase("PruebaMongo").getCollection("GameItem").countDocuments();
            if (c == 0) {
                gameService.cargarJuegosDesdeJson("src/main/resources/out.json");
            } else {
                System.out.println("Juegos ya cargados en MongoDB (" + c + "), saltando carga.");
            }
        };
    }
}