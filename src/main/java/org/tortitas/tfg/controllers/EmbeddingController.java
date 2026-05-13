package org.tortitas.tfg.controllers;

import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmbeddingController {
    private final OllamaEmbeddingModel ollamaEmbeddingModel;

    @Autowired
    public EmbeddingController(OllamaEmbeddingModel ollamaEmbeddingModel) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
    }

    @GetMapping("/ai/embedding")
    public float[] embed(Document document){
        return this.ollamaEmbeddingModel.embed(document);
    }
}
