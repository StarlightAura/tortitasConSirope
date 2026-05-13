package org.tortitas.tfg.models;

import jakarta.persistence.*;
import lombok.*;
import org.tortitas.tfg.config.Token;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "USERS") /*LE PUSE EL NOMBRE EN INGLÉS PARA QUE SIGA UN SOLO IDIOMA */
public class User {
    @Id
    @Column(length = 50, unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(length = 5)
    @Enumerated(EnumType.STRING)
    private Rol role = Rol.USER;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL ,fetch = FetchType.LAZY)
    private List<Token> tokens = new ArrayList<>();

}