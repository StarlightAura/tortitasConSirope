package org.tortitas.tfg.mapper;

import org.tortitas.tfg.dto.UserRequestWebDTO;
import org.tortitas.tfg.models.User;

public class UserMapper {

    public static User toUser(UserRequestWebDTO dto){
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .build();
    }
}
