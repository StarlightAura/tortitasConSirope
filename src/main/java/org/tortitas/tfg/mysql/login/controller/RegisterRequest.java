package org.tfg.api.mysql.login.controller;

public record RegisterRequest(
        String email,
        String password,
        String name
) {


}
