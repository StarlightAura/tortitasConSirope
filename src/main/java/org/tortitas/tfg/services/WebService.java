package org.tortitas.tfg.services;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.exception.IncorrectPasswordException;
import org.tortitas.tfg.exception.NoTokenPresentException;
import org.tortitas.tfg.exception.UserNotFoundException;
import org.tortitas.tfg.mapper.UserMapper;
import org.tortitas.tfg.models.JWTToken;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class WebService {

    private final JWTToken jwtToken;
    private final GameService gameService;
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
    public HttpSession signin(UserRequestDTO dto, HttpSession session) throws JoseException {

        final User user = findByNameInternal(dto.getName());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())){
            throw new IncorrectPasswordException("Incorrect password.");
        }

        final String token = jwtToken.generateToken(user.getNombreUser(), user.getRole());

        session.setAttribute("token", token);
        session.setAttribute("username", user.getNombreUser());
        session.setAttribute("rol", user.getRole().name());

        return session;
    }

    //Consult Database
    public User findByNameInternal(String name){
        return repository
                .findByNombreUser(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    //Recommendations
    public Model recommendation(String game,HttpSession session, Model model) throws JoseException {

        validateSesion(session);
        String token = (String) session.getAttribute("token");
        if (!jwtToken.isTokenValid(token)){
            throw new JoseException("No valid token");
        }

        List<String>recommendations = gameService.recomendar(game);
        model.addAttribute("recomendaciones", recommendations); //envia los datos al html
        model.addAttribute("query", game); //guarda la busqueda
        model.addAttribute("username", session.getAttribute("username"));
        model.addAttribute("rol", session.getAttribute("rol"));

        return model;

    }

    public void validateSesion(HttpSession session){
        if (session.getAttribute("token")==null){
            throw new NoTokenPresentException("No token present.");
        }
    }


}
