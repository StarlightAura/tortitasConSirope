package org.tfg.api.mysql.login.mapper;

import org.tfg.api.mysql.login.dto.UserCreateDTO;
import org.tfg.api.mysql.login.dto.UserResponseDTO;
import org.tfg.api.mysql.login.model.Role;
import org.tfg.api.mysql.login.model.User;

public class UserMapper {

    public static UserResponseDTO toDto(User user){
        return new UserResponseDTO(user.getName(), user.getEmail(), user.getRole());
    }

    public static User toUser(UserCreateDTO dto){
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .role(Role.USER)
                .build();
    }

    public static User toAdmin(UserCreateDTO dto){
        return User.builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .role(Role.ADMIN)
                .build();
    }



}
