package org.tortitas.tfg.mysql.login.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.tortitas.tfg.mysql.login.validation.ValidPassword;

public record RegisterRequest(
        @NotBlank
        @Email(message = "Formato de email incorrecto.")
        String email,

        @NotBlank
        @ValidPassword
        String password,

        @NotBlank
        @Size(min = 2, max = 50)
        String name
) {


}
