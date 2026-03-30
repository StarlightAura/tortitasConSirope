package org.tortitas.tfg.mysql.login.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.tortitas.tfg.mysql.login.model.Role;


@Getter
@AllArgsConstructor
public class UserResponseDTO {
    private String name;
    private String email;
    private Role role;
}
