package org.tfg.api.mysql.login.service;

import org.springframework.stereotype.Service;
import org.tfg.api.mysql.login.Exception.UserNotFoundException;
import org.tfg.api.mysql.login.dto.UserResponseDTO;
import org.tfg.api.mysql.login.mapper.UserMapper;
import org.tfg.api.mysql.login.model.User;
import org.tfg.api.mysql.login.repository.UserRepository;

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
