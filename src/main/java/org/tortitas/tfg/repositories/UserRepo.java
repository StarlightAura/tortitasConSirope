package org.tortitas.tfg.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.tortitas.tfg.models.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
}
