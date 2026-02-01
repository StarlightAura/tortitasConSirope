package org.tortitas.tfg.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.models.Game;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Slf4j
@Service
public class OllamaService {

    //@Value("${ollama.api.url:http://localhost:11434}")
    //private OllamaChatModel ollamaChatModel;
    private final OllamaEmbeddingModel ollamaEmbeddingModel;


    //TODO: fix this ollama bean (somehow)

    public OllamaService(OllamaEmbeddingModel ollamaEmbeddingModel) {
        this.ollamaEmbeddingModel = ollamaEmbeddingModel;
    }

    public List<String> objectToString(Game game){
        List<String> inputs = new ArrayList<>();
        inputs.add("query: ");
        inputs.add(game.toString());
        return inputs;
    }

    public Vector<Double> getAnswer(Game game){

        List<String> inputs = objectToString(game);

        log.info("Recieved: {}", inputs);
        EmbeddingRequest embeddingRequest = new EmbeddingRequest(inputs, null);
        EmbeddingResponse embeddingResponse = ollamaEmbeddingModel.call(embeddingRequest);
        log.info("Response: {}", embeddingResponse);
        float[] arr = embeddingResponse.getResult().getOutput();

        Vector<Double> v = new Vector<>();
        IntStream.range(0, arr.length).mapToDouble(i -> arr[i]).forEach(v::add);

        return v;

    }

}
