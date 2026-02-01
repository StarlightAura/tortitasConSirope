package org.tortitas.tfg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tortitas.tfg.models.Game;
import org.tortitas.tfg.services.OllamaService;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

@RestController
@RequestMapping("/api/v1/ollama")
public class OllamaController {

    private OllamaService ollamaService;

    @Autowired
    public OllamaController(OllamaService ollamaService) {
        this.ollamaService = ollamaService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json", path = "/prompt")
    public Vector<Double> getQwen3Response(@RequestBody String input) {
        return ollamaService.getAnswer(new Game());
    }
}
