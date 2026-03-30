package org.tfg.api.mysql.login.controller;

public record LoginRequest(
        String email,
        String password
) {
}
