package org.tortitas.tfg.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.dto.CreateUserDTO;
import org.tortitas.tfg.dto.ResponseUserDTO;
import org.tortitas.tfg.dto.UpdateUserPasswordDTO;
import org.tortitas.tfg.mapper.UserMapper;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

    //CREATE
    public void create(CreateUserDTO dto){

        Optional<User> optionalUser = repository.findByNombreUser(dto.getName());

        if (optionalUser.isPresent()){
            throw new IllegalStateException("User already exist.");
        }

        User user = UserMapper.toUser(dto);
        repository.save(user);
    }

    //READ
    public ResponseUserDTO findByName(String name){
        User user = repository
                .findByNombreUser(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toDto(user);
    }

    public User findByNameInternal(String name){
        return repository
                .findByNombreUser(name)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> allUsersInternal(){
        return repository.findAll();
    }

    public List<ResponseUserDTO> allUsers(){
        return allUsersInternal()
                .stream()
                .map(UserMapper::toDto)
                .toList();
    }

    //UPDATE
    @Transactional
    public void updatePassword(String name,UpdateUserPasswordDTO dto){
        User user = findByNameInternal(name);

        user.setPassword(dto.getPassword());

        repository.save(user);
    }

    //DELETE
    public void delete(String name){
        User user = findByNameInternal(name);
        repository.delete(user);
    }

}
