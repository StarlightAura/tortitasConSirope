package org.tortitas.tfg;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.repositories.GameRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;

@SpringBootApplication
@EnableMongoRepositories
public class TfgApplication implements CommandLineRunner {

    @Autowired
    GameRepository gameRepository;

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

    public void createGames() throws FileNotFoundException {
        Gson gson = new Gson();
        List<Game> games = gson.fromJson(new FileReader("/home/aura/IDEAProjects/tortitasConSirope/src/main/resources/out.json"), new TypeToken<List<Game>>() {});
        gameRepository.saveAll(games);
    }

}
