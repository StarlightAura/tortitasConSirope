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

/**
 *Clase principal y punto de entrada de la aplicacion.
 *<p>
 *Se encarga de arrancar el ecosistema del proyecto (servicios, seguridad, repositorios, etc.).
 *Ademas, incluye una logica de inicializacion que comprueba si la base de datos de MongoDB
 *esta vacia para realizar una carga automatica del catalogo de juegos desde un archivo JSON local.
 *</p>
 */
@SpringBootApplication
@EnableMongoRepositories //Habilita de forma explicita el uso de repositorios de Spring Data MongoDB
public class TfgApplication {
    /**Conexion directa con el almacenamiento vectorial de MongoDB Atlas.*/
    @Autowired MongoDBAtlasVectorStore vectorStore;

    /**Inyectamos el servicio de juegos para gestionar la lectura e insercion por lotes del JSON.*/
    @Autowired GameService gameService;

    /**Recupera la cadena de conexion de MongoDB directamente desde el archivo 'application.properties'.*/
    @Value("${spring.mongodb.uri}")
    String uri;

    public static void main(String[] args) {
        SpringApplication.run(TfgApplication.class, args);
    }
    /**
     *Tarea que se ejecuta de forma automatica inmediatamente despues de que la app termine de arrancar.
     *<p>
     *Se conecta directamente a la coleccion de MongoDB, cuenta cuantos documentos hay guardados y, si la base de datos
     *esta totalmente vacia, inicia la lectura y vectorizacion del archivo JSON del catalogo de juegos.
     *En caso contrario, salta la carga y continua la ejecucion normal.
     *</p>
     * @return Una instancia de {@link CommandLineRunner} que Spring gestionara de forma automatica.
     */
    @Bean
    public CommandLineRunner cargarDatos() {
        return args -> {
            //Nos conectamos de forma nativa a la base de datos "PruebaMongo" y a su coleccion "GameItem"
            //para contar la cantidad exacta de documentos que existen actualmente.
            long c = MongoClients.create(uri).getDatabase("PruebaMongo").getCollection("GameItem").countDocuments();
            //Luego se evalua el estado de la base de datos
            if (c == 0) {
                //Si la base de datos de MongoDB esta vacia, cargamos el archivo JSON
                gameService.cargarJuegosDesdeJson("src/main/resources/out.json");
            } else {
                //Si no esta vacia, continua la ejecucion de forma normal
                System.out.println("Juegos ya cargados en MongoDB (" + c + "), saltando carga.");
            }
        };
    }
}