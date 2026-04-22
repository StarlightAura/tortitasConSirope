package org.tortitas.tfg.mapper;

import org.tortitas.tfg.dto.UserRequestDTO;
import org.tortitas.tfg.dto.ResponseUserDTO;
import org.tortitas.tfg.models.User;

public class UserMapper {

    public static User toUser(UserRequestDTO dto){
        return User.builder()
                .nombreUser(dto.getName())
                .password(dto.getPassword())
                .build();
    }

    public static ResponseUserDTO toDto(User user){
        return new ResponseUserDTO(
                user.getChatId(),
                user.getNombreUser()
        );
    }


}
