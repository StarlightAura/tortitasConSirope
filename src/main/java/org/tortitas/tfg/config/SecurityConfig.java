package org.tortitas.tfg.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session
                        ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //VOY A HACER MÁS PRUEBAS PORQUE NO SE USA JWT COMO TAL POR LO QUE PUEDE NO SER NECESARIO

                .formLogin(AbstractHttpConfigurer::disable) //si no desactivo el fomulario que viene por defecto da error

                .authenticationProvider(authenticationProvider) //AYUDA A PONER AL USUARIO DENTRO DEL CONTEXTO
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) //REGISTRANDO DEL FILTRO YA QUE SI NO SPRING LO SALTA

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/img/**","/api/auth/**").permitAll() //los endpoints para el log y registrarse son publicos
                        .requestMatchers("/", "/login", "/web/signin", "/web/signup").permitAll()
                        .requestMatchers("/home", "/web/**").permitAll()

                        .anyRequest()
                        .authenticated()

                );
        return http.build();
    }
}

//ES IMPORTANTE MENCIONAR QUE EL FILTRO SOLO FUNCIONA ACCEDIENDO DESDE POSTMAN, EL ACCESO POR EL NAVEGADOR SE HACE MEDIANTEÇ
//SESIONES POR LO QUE EL FILTRO NO TIENE EFECTO DESDE ALLÌ
// TRASLADÉ EL "passwordEncoder"A LA CLASE "AppConfig" PORQUE SEGUÍ EL TUTORIAL XD
