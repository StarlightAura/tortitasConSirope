package org.tortitas.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.tortitas.tfg.config.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {

    @Query("Select t FROM Token t WHERE t.user.name = :name and (t.expired=false and t.revoked=false)")
    List<Token> findAllValidTokensByUser(String name);


    Optional<Token> findByToken(String token);


}
