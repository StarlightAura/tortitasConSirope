package org.tortitas.tfg;

import java.util.Vector;
import java.util.stream.IntStream;

public class CosineSimilarity {

    public static double cosineSimilarity(Vector<Double> vectorA, Vector<Double> vectorB) {

        if (vectorA.size() != vectorB.size()) {
            throw new IllegalArgumentException("Vectors do not have the same size.");
        }

        double dotProduct = IntStream.range(0, vectorA.size()).mapToDouble(i -> vectorA.get(i) * vectorB.get(i)).sum();
        double normA = Math.sqrt(vectorA.stream().mapToDouble(i -> i * i).sum());
        double normB = Math.sqrt(vectorB.stream().mapToDouble(i -> i * i).sum());

        if (normA == 0 || normB == 0) {
            throw new IllegalArgumentException("Vector magnitude cannot be zero.");
        }

        return dotProduct / (normA * normB);
    }
}
