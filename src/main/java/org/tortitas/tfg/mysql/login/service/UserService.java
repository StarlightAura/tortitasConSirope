package org.tortitas.tfg.mysql.login.service;

import org.springframework.stereotype.Service;
import org.tortitas.tfg.mysql.login.repository.UserRepository;


@Service
public class UserService {

    private UserRepository repository;

    public UserService(UserRepository repository){
        this.repository = repository;
    }

}
