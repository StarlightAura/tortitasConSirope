package org.tfg.api.mysql.login.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserUpdatePasswordDTO {
    @NotBlank (message = "No ingresaste la contraseña")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "La contraseña debe tener almenos 8 caracteres formada por mayúsculas, minúsculas, número y símbolo"
    )
    private String password;
}
