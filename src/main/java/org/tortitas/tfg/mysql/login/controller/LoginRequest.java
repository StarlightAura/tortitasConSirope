package org.tortitas.tfg.mysql.login.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.tortitas.tfg.mysql.login.validation.ValidPassword;

public record LoginRequest(
        @NotBlank
        @Email(message = "Formato de email incorrecto.")
        String email,

        @NotBlank
        @ValidPassword
        String password
) {
}
