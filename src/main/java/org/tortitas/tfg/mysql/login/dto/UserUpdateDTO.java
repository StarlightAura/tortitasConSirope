package org.tfg.api.mysql.login.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.tfg.api.mysql.login.model.Role;

@Builder
@Getter
@AllArgsConstructor
public class UserUpdateDTO {
    @Size(min = 2, max = 50)
    private String name;

    private Role role;
}
