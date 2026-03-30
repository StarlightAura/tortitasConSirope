package org.tfg.api.mysql.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.tfg.api.mysql.login.model.Role;

@Getter
@AllArgsConstructor
public class UserCreateDTO {
    @NotBlank
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "La contraseña debe tener almenos 8 caracteres formada por mayúsculas, minúsculas, número y símbolo"
    )
    private String password;

    @NotNull
    private Role role;

}
