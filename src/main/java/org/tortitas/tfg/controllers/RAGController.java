package org.tortitas.tfg.controllers;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.ai.vectorstore.mongodb.atlas.MongoDBAtlasVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/ai")
public class RAGController {

    @Autowired
    private MongoDBAtlasVectorStore vectorStore;

    @Autowired
    private OllamaEmbeddingModel ollamaEmbeddingModel;

    @Autowired
    public RAGController(OllamaEmbeddingModel ollamaEmbeddingModel, MongoDBAtlasVectorStore vectorStore) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
        this.vectorStore = vectorStore;
    }

    @GetMapping("/embedding")
    public float[] embed(Document document){
        return this.ollamaEmbeddingModel.embed(document);
    }
}
