package org.tortitas.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tortitas.tfg.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByNombreUser(String nombreUser);
    Optional<User> findByChatId(Long chatId);
}
