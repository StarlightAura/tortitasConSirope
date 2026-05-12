package org.tortitas.tfg.services;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.models.Rol;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepo;

@Service
public class UserService {
    @Autowired private UserRepo userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTToken jwtToken;

    //La comprobacion del usuario se quita del controller y se mete aqui. En el controller solo se llama a esta funcion
    public void registrarUser(String nombre, String pass) {
        if (userRepo.findByNombreUser(nombre).isPresent()) {
            throw new RuntimeException("El usuario ya existe.");
        }
        User user = new User();
        user.setNombreUser(nombre);
        user.setPassword(passwordEncoder.encode(pass));
        user.setRol(Rol.USER);
        userRepo.save(user);
    }

    //La comprobacion del nombre y contraseña ahora se hacen en el service al igual que la funcion anterior
    public String verificarSignin(String nombreUser, String password) throws JoseException {
        User user = userRepo.findByNombreUser(nombreUser).orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        return jwtToken.generateToken(user.getNombreUser(), user.getRol());
    }

    public Rol obtenerRol(String nombreUser) {
        return userRepo.findByNombreUser(nombreUser)
                .map(User::getRol)
                .orElse(Rol.USER);
    }
}
