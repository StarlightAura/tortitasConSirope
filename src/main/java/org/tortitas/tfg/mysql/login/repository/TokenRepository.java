package org.tfg.api.mysql.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Long> {


     @Query("Select t FROM Token t WHERE t.user.id = :uid and (t.expired=false and t.revoked=false)")
    List<Token> findAllValidTokensByUser(Long uid);


    Optional<Token> findByToken(String token);
}
