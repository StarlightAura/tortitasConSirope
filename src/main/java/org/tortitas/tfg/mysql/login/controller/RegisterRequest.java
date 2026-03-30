package org.tortitas.tfg.mysql.login.controller;

public record RegisterRequest(
        String email,
        String password,
        String name
) {


}
