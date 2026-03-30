package org.tortitas.tfg.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatId;

    @Column(unique = true, nullable = false)
    private String nombreUser;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol;
}