package org.tortitas.tfg.mysql.login.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tortitas.tfg.mysql.login.dto.UserResponseDTO;
import org.tortitas.tfg.mysql.login.service.AdminService;


import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final AdminService service;

    public ResponseEntity<List<UserResponseDTO>> getUsers(){
        return ResponseEntity.ok(service.allUsers());
    }



}
