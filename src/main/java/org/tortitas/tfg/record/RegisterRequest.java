package org.tortitas.tfg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

    @NotBlank
    @Size(min = 2, max = 30)
    String  name,

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$",
            message = "La contraseña debe tener almenos 8 caracteres formada por mayúsculas, minúsculas, número y símbolo")
    String password
) {

}
