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
    EmbeddingController embeddingController;

    @Autowired
    VectorStore vectorStore;

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
//    public void run(String... args){
//
//        try{
//            createGames();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//    }

//    public void createGames() throws FileNotFoundException {
//        Gson gson = new Gson();
//        List<Game> games = gson.fromJson(new FileReader("/home/aura/IDEAProjects/tortitasConSirope/src/main/resources/out.json"), new TypeToken<List<Game>>() {});
//
//
//
//        for (Game game : games) {
//            float[] embeddings = embeddingController.embed(game.game2document());
//            //System.out.println(Arrays.toString(embeddings));
//
//            double[] dEmbeddings = IntStream.range(0, embeddings.length).mapToDouble(i -> embeddings[i]).toArray();
//            List<Double> d2Embeddings = Arrays.stream(dEmbeddings).boxed().toList();
//
//            Vector<Double> vEmbeddings = new Vector<Double>(d2Embeddings);
//            game.setEmbeddings(vEmbeddings);
//
//
//
//            //TODO: fix the document mess
//            /*
//            String insertedId;
//
//            try{
//                InsertOneResult result = collection.insertOne(document);
//                System.out.println("Inserted id: " + result.getInsertedId());
//            } catch (MongoException me){
//                throw new RuntimeException("Error inserting games", me);
//            }
//            */
//
//            System.out.println(game);
//
//            gameRepository.save(game);
//
//        }
//
//        //gameRepository.saveAll(games);

