package org.tortitas.tfg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.tortitas.tfg.exception.InvalidTokenException;
import org.tortitas.tfg.repositories.TokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTToken jwtToken;
    private final JwtAuthFilter jwtAuthFilter;
    private final TokenRepository tokenRepository;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session
                        ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //Indica que la seguridad está basada en tokens

                .formLogin(AbstractHttpConfigurer::disable) //Si no desactivo el fomulario que viene por defecto da error

                .authenticationProvider(authenticationProvider) //Proveedor de autenticación de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) //Registramos el filtro

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/img/**","/api/auth/**").permitAll() //los endpoints para el log y registrarse son publicos
                        .requestMatchers("/", "/login", "/web/signin", "/web/signup").permitAll()
                        .requestMatchers("/home", "/web/**").permitAll()

                        .anyRequest()
                        .authenticated()

                )

                .logout(logout ->
                        logout.logoutUrl("/auth/logout")
                                .addLogoutHandler((request, response, authentication) -> {
                                    final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                    logout(authHeader);
                                })
                                .logoutSuccessHandler( (request, response, authentication) ->
                                        SecurityContextHolder.clearContext())
                );
        return http.build();
    }

    private void logout(final String token){
        if (token==null || !token.startsWith("Bearer ")){
            throw new InvalidTokenException("Invalid token");
        }

        final String jwtTokenSecurity = token.substring(7);

        if (!jwtToken.isTokenValid(jwtTokenSecurity)){
            throw new InvalidTokenException("Invalid token");
        }

        final Token foundToken = tokenRepository.findByToken(jwtTokenSecurity)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));


        foundToken.setExpired(true);
        foundToken.setRevoked(true);
        tokenRepository.save(foundToken);
    }


}

/*
* Elementos agregados recientemente
*
* 1. @EnableMethodSecurity: Esta anotación ayuda a activar todos aquellos métodos que requieran pre-autorización, por ejemplo:
* la anotación @PreAuthorize la cual recoge la información del usuario autenticado y mira su rol
*
* 2. authenticationProvider y addFilterBefore, estos métodos nos ayudan a que el usuario que está intentando acceder pase por
* un por la cadena de filtros de Spring y que se registren sus credenciales hasta que este decida hacer logout
*
*3. ".anyRequest().authenticated()" Indica a Spring que después de los endpoints declarados como públicos toda solicutud deberá
* ser atendida solo si el usuario está autenticado
*
*4. ".logout" ayuda a desactivar el token al momento que el usuario decida salir, obligando a que ese token sea reconocido como
* expirado de forma automática sin esperar que este expire con el tiempo.
* */
