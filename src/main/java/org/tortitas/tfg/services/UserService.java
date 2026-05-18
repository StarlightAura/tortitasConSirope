package org.tortitas.tfg.services;

import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.models.Rol;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.UserRepo;

/**
 *Servicio encargado de la gestion de usuarios, registro y autenticacion.
 *<p>
 *Este servicio centraliza la logica de seguridad de las cuentas de los usuarios.
 *Se encarga de validar los inicios de sesion comparando las contraseñas,
 *generar los tokens JWT de sesión y controlar que no haya usuarios duplicados
 *en la base de datos al registrarse.
 *</p>
 *@author Laura Martín Martínez
 *@author StarlightAura
 *@author Prabhnoor Singh Kaur
 */
@Service
public class UserService {
    @Autowired private UserRepo userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JWTToken jwtToken;

    /**
     *Estructura ligera (usando Record) para transportar el resultado de un inicio de sesion correcto.
     *<p>
     *El objetivo de esto fue para poder almacenar datos de forma rapida y concisa con el fin
     *de eliminar el codigo repetitivo como getters, setters, etc.
     *</p>
     */
    public record SigninResult(String token, String rol) {}

    /**
     *Verifica las credenciales de un usuario que intenta iniciar sesion.
     *<p>
     *Busca al usuario en la base de datos y comprueba la contraseña usando el passwordEncoder.
     *Si coincide en su totalidad, genera un token JWT firmado y lo devuelve junto con su rol.
     *</p>
     *@param nombreUser Nombre de usuario introducido en el login.
     *@param password Contraseña introducida en texto plano.
     *@return Un nuevo objeto {@link SigninResult} con el token y el rol asignado.
     *@throws JoseException Si ocurre un error criptografico al generar el token JWT.
     *@throws RuntimeException Si el usuario no existe o la contraseña no coincide.
     */
    public SigninResult verificarSignin(String nombreUser, String password) throws JoseException {
        //Buscamos al usuario por su nombre. Si no existe, lanzamos un error de credenciales.
        User user = userRepo.findByNombreUser(nombreUser)
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));
        //Comprobamos si la contraseña introducida coincide con el hash guardado en la base de datos.
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        //Si la contraseña es correcta, generamos el token con nuestro servicio criptografico
        String token = jwtToken.generateToken(user.getNombreUser(), user.getRol());
        //Devolvemos el record con el token y el nombre plano del rol
        return new SigninResult(token, user.getRol().name());
    }

    /**
     *Registra un nuevo usuario en el sistema con el rol comun por defecto (es decir, USER).
     *<p>
     *Comprueba primero que el nombre no este ya registrado en la base de datos. Si esta libre,
     *pasa la contraseña por el encriptador para generar su hash seguro.
     *</p>
     *@param nombre El nombre de usuario elegido.
     *@param pass La contraseña en texto plano que ha elegido el mismo usuario.
     *@throws RuntimeException Si el nombre de usuario ya esta registrado en el sistema.
     */
    public void registrarUser(String nombre, String pass) {
        //Verificamos si ya existe alguien con ese mismo nombre en la base de datos
        if (userRepo.findByNombreUser(nombre).isPresent()) {
            throw new RuntimeException("El usuario ya existe.");
        }
        //Creamos la nueva entidad de usuario y guardamos sus propiedades
        User user = new User();
        user.setNombreUser(nombre);
        //Encriptamos la contraseña antes de guardarla
        user.setPassword(passwordEncoder.encode(pass));
        //Por defecto, cualquier usuario que se registre de forma publica tendra el rol basico 'USER'
        user.setRol(Rol.USER);
        //Guardamos el nuevo usuario en la base de datos
        userRepo.save(user);
    }
}
