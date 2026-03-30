package org.tortitas.tfg.mysql.login.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.boot.context.properties.ConfigurationProperties;



@Getter
@AllArgsConstructor

@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtConfig {


    private final String secret;

    private final long expiration;

    private final long refreshTokenExpiration;


}
