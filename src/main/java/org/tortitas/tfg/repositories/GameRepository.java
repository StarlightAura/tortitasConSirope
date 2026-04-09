package org.tortitas.tfg.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.tortitas.tfg.models.Game;

public interface GameRepository extends MongoRepository<Game, Integer> {
}
