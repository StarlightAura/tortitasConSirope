package org.tortitas.tfg.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class CreateUserDTO {
    private final String name;
    private final String password;
    private final String regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

    public CreateUserDTO(String name, String password){
        if (name.length()<2){
            throw new IllegalArgumentException("Nombre de usuario demasiado corto");
        }

        if (!password.matches(regexp)){
            throw new IllegalArgumentException(
                    "La contraseña debe tener almenos 8 caracteres formada por" +
                    " mayúsculas, minúsculas, número y símbolo"
            );
        }

        this.name = name;
        this.password = password;

    }


}
