package org.tortitas.tfg.dto;

import lombok.Getter;

@Getter
public class UserRequestWebDTO {
    private final String name;
    private final String password;
    private final String REGEXP = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$";

    public UserRequestWebDTO(String name, String password){
        if (name.length()<2){
            throw new IllegalArgumentException("Nombre de usuario demasiado corto");
        }

        if (!password.matches(REGEXP)){
            throw new IllegalArgumentException(
                    "La contraseña debe tener almenos 8 caracteres formada por" +
                    " mayúsculas, minúsculas, número y símbolo"
            );
        }

        this.name = name;
        this.password = password;

    }


}
