package org.tfg.api.mysql.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.tfg.api.mysql.login.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

}
