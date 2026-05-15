package org.tortitas.tfg.controllers;

import com.mongodb.client.model.search.FieldSearchPath;
import org.bson.conversions.Bson;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.services.GameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.search.FieldSearchPath;
import org.bson.conversions.Bson;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.vectorSearch;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.metaVectorSearchScore;
import static com.mongodb.client.model.search.SearchPath.fieldPath;
import static com.mongodb.client.model.search.VectorSearchOptions.approximateVectorSearchOptions;
import static java.util.Arrays.asList;


@RestController
@RequestMapping("/ai")
public class RAGController {

    @Value("${spring.mongodb.uri}")
    private String uri;

    @Autowired
    private MongoDBAtlasVectorStore vectorStore;

    private final OllamaEmbeddingModel ollamaEmbeddingModel;
    @Autowired
    private GameService gameService;
    //private final MongoDBAtlasVectorStore vectorStore;

    @Autowired
    public RAGController(OllamaEmbeddingModel ollamaEmbeddingModel, MongoDBAtlasVectorStore vectorStore) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/embedding")
    public float[] embed(Document document){
        return this.ollamaEmbeddingModel.embed(document);
    }

    @GetMapping("/vectorSearch")
    public List<Document> searchDocuments(@RequestParam String s){

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query(s)
                        .topK(2).build()
        );

        /*return results.stream().map(doc -> Map.of(
                "content", doc.getFormattedContent(),
                "metadata", doc.getMetadata()
        )).collect(Collectors.toList());*/


        return results;


        /*MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("PruebaMongo");
        MongoCollection<org.bson.Document> collection = database.getCollection("vector_index");


        List<Double> queryVector = gameService.vectorizar("jueguitos de los 90");
        String indexName = "vector_index";
        FieldSearchPath fieldSearchPath = fieldPath("plot_embedding_voyage_3_large");
        int limit = 10;
        int numCandidates = 150;

        List<String> l = new ArrayList<>();

        List<Bson> pipeline = asList(
                vectorSearch(
                        fieldSearchPath,
                        queryVector,
                        indexName,
                        limit,
                        approximateVectorSearchOptions(numCandidates)),
                project(
                        fields(exclude("_id"), include("name"), include("description"), include("developers"), include("languages"), include("genres"), include("tags"))));

        // run query and print results
        collection.aggregate(pipeline)
                .forEach(doc -> l.add(doc.toJson()));

        return l;*/
    }
}
