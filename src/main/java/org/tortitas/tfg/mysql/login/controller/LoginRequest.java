package org.tortitas.tfg.mysql.login.controller;

public record LoginRequest(
        String email,
        String password
) {
}
