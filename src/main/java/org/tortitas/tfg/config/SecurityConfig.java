package org.tortitas.tfg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.tortitas.tfg.models.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable()) //si no desactivo el fomulario que viene por defecto da error
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**","/img/**","/api/auth/**").permitAll() //los endpoints para el log y registrarse son publicos
                        .requestMatchers("/", "/login", "/web/signin", "/web/signup").permitAll()
                        .requestMatchers("/home", "/web/**").permitAll()
                        .requestMatchers("/api/**").permitAll() //cambio necesario para los endpoint de los controller game y web
                        .requestMatchers("/ai/**").permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}