package org.tortitas.tfg.controllers;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.tortitas.tfg.models.Game;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
public class RAGController {

    @Autowired
    private VectorStore vectorStore;

    private final OllamaEmbeddingModel ollamaEmbeddingModel;
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
    public List<Map<String, Object>> searchDocuments(){

        List<Document> results = vectorStore.similaritySearch(
                SearchRequest.builder().query("videojuegos de los 90")
                        .topK(2).build()
        );

        return results.stream().map(doc -> Map.of(
                "content", doc.getFormattedContent(),
                "metadata", doc.getMetadata()
        )).collect(Collectors.toList());
    }
}
