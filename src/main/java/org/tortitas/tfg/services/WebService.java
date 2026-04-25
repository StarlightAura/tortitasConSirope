package org.tortitas.tfg.services;

import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.exception.IncorrectPasswordException;
import org.tortitas.tfg.exception.UserNotFoundException;
import org.tortitas.tfg.mapper.UserMapper;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class WebService {

    private final JWTToken jwtToken;
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    //Registry
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

    //Consult Database
    public User findByNameInternal(String name){
        return repository
                .findByNombreUser(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    //Recommendations




}
