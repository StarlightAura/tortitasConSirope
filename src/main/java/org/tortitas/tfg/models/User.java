package org.tortitas.tfg.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *Entidad de dominio que representa a los usuarios en la base de datos relacional.
 *<p>
 *Mapea de forma automatica los datos contra la tabla "Usuarios" de PostgreSQL mediante JPA,
 *sirviendo como estructura base para la autenticacion en Spring Security.
 *</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Usuarios") //Para decirle a JPA que esta clase es una tabla de la BD llamada "Usuarios"
public class User {
    /**Nombre de usuario unico que actua como PK.*/
    @Id
    @Column(unique = true, nullable = false)
    private String nombreUser;

    /**Contraseña del usuario (en la base de datos se almacenara su hash de BCrypt).*/
    @Column(nullable = false)
    private String password;

    /**
     *Rol asignado para el control de accesos y permisos en el sistema.
     *<p>
     *Se almacena como texto plano (STRING) en la base de datos y se inicializa
     *por defecto con el valor 'USER'.
     *</p>
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default //esto es para que no se quede a null si no se especifica el rol
    private Rol rol = Rol.USER; //por defecto sera 'USER'
}