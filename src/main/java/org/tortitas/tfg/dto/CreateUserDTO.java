package org.tortitas.tfg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateUserDTO {

    @NotBlank
    @Size(min = 5, max = 30)
    private final String name;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "La contraseña debe tener almenos 8 caracteres formada por mayúsculas, minúsculas, número y símbolo"
    )
    private final String password;

}
