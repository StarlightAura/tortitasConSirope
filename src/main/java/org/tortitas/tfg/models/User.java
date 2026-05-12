package org.tortitas.tfg.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "USERS")
public class User {
    @Id
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    /*NO LE PUSE LENGTH PORQUE NO ESTOY SEGURA CUANTOS CARACTERES TIENE*/
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(length = 5)
    @Enumerated(EnumType.STRING)
    private Rol role = Rol.USER;
}