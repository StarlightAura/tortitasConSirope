package org.tortitas.tfg.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.dto.ResponseUserDTO;
import org.tortitas.tfg.dto.UpdateUserPasswordDTO;
import org.tortitas.tfg.exception.IncorrectPasswordException;
import org.tortitas.tfg.exception.UserNotFoundException;
import org.tortitas.tfg.mapper.UserMapper;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {

    private final JWTToken jwtToken;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    //CREATE
    public void signup(UserRequestDTO dto){

        Optional<User> optionalUser = repository.findByNombreUser(dto.getName());

        if (optionalUser.isPresent()){
            throw new IllegalStateException("User already exist.");
        }

        User user = UserMapper.toUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.save(user);
    }

    //Login
    public String signin(UserRequestDTO dto) throws JoseException {

        final User user = findByNameInternal(dto.getName());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new IncorrectPasswordException("Incorrect password.");
        }

        return jwtToken.generateToken(user.getNombreUser());
    }

    //READ
    public ResponseUserDTO findByName(String name){
        User user = repository
                .findByNombreUser(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDto(user);
    }

    public User findByNameInternal(String name){
        return repository
                .findByNombreUser(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
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
