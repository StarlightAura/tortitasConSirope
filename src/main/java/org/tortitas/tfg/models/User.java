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

@Entity
@Table(name = "Usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer chatId;

    @Column(length = 50, unique = true, nullable = false)
    private String nombreUser;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(length = 10)
    @Enumerated(EnumType.STRING)
    private Rol role = Rol.USER;
}