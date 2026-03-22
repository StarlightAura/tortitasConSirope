package org.tortitas.tfg;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.MongoException;
import com.mongodb.client.result.InsertOneResult;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.controllers.EmbeddingController;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.repositories.GameRepository;
import org.tortitas.tfg.services.OllamaService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@SpringBootApplication
@EnableMongoRepositories
public class TfgApplication implements CommandLineRunner {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    EmbeddingController embeddingController;

    @Autowired
    VectorStore vectorStore;

    public static void main(String[] args) {
        SpringApplication.run(TfgApplication.class, args);
    }

    public void run(String... args){

        try{
            createGames();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    //PROBLEMITA, ESTO NO NOS SIRVE PARA LA CANTIDAD INGENTE DE JUEGOS QUE TENEMOS
   public void createGames() throws FileNotFoundException {
        Gson gson = new Gson();
        List<Game> games = gson.fromJson(
                new FileReader("out.json"),
                new TypeToken<List<Game>>() {}
        );

        for (Game game : games) {
            /*Aqui es donde se volveria un poco crazy si le pasamos lo de antes
            * por eso el format para llevar de la manita a ollama creando un texto que entienda
            * y asi devuelva los vectores para que todos estemos contentos*/
            float[] raw = embeddingController.embed(game.game2document());

            //Just un bucle que traduce los floats de 32b de ollama a los 64b que usamos
            Vector<Double> embeddingVector = new Vector<>();
            for (float f : raw) {
                embeddingVector.add((double) f);
            }

            game.embeddings = embeddingVector; //asigna vectores a nuestro objetito
            gameRepository.save(game);
            System.out.println("Se ha guardado el jueguito -> " + game.name);
        }
    }

}