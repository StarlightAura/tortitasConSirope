package org.tortitas.tfg.mysql.login.service;

import org.springframework.stereotype.Service;

import org.tfg.api.mysql.login.dto.UserResponseDTO;

import org.tortitas.tfg.mysql.login.Exception.UserNotFoundException;
import org.tortitas.tfg.mysql.login.mapper.UserMapper;
import org.tortitas.tfg.mysql.login.model.User;
import org.tortitas.tfg.mysql.login.repository.UserRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final UserRepository repository;

    public AdminService (UserRepository repository){
        this.repository = repository;
    }

    public List<UserResponseDTO> allUsers(){

        List<UserResponseDTO> response = new ArrayList<>();

        repository.findAll()
                .stream().map(UserMapper::toDto)
                .forEach(response::add);

        return response;
    }

    public UserResponseDTO findUserByName(String name){

        Optional<User> userOpt = repository.findByName(name);

        if (userOpt.isEmpty()){
            throw new UserNotFoundException("User not found.");
        }

        return UserMapper.toDto(userOpt.get());
    }



}
