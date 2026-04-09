package org.tortitas.tfg.mysql.login.mapper;


import org.tortitas.tfg.mysql.login.dto.UserResponseDTO;
import org.tortitas.tfg.mysql.login.model.Role;
import org.tortitas.tfg.mysql.login.model.User;


public class UserMapper {

    public static UserResponseDTO toDto(User user){
        return new UserResponseDTO(user.getName(), user.getEmail(), user.getRole());
    }
}
