package org.tortitas.tfg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/auth/**").permitAll() // Permitir registro y login
                        .requestMatchers("/web/admin/**").hasRole("ADMIN")       // Solo Admin
                        .anyRequest().authenticated()                            // El resto pide login
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")            // Nuestra página
                        .loginProcessingUrl("/auth/login")   // Donde enviamos el POST
                        .defaultSuccessUrl("/web/juegos", true)    // Donde vamos al entrar
                        .permitAll()
                )
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
   /* @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //http.csrf(csrf -> csrf.disable())
               // .authorizeHttpRequests(auth -> auth
                        //.requestMatchers("/api/auth/**").permitAll()
                        //.anyRequest().permitAll() // el JWT lo validamos nosotros en el controller
               // );
       // return http.build();
        http.csrf(csrf -> csrf.disable()) // Mantenerlo deshabilitado para pruebas
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/admin/**").hasRole("ADMIN") // Solo el admin entra aquí
                        .requestMatchers("/web/juegos").permitAll()       // Todos ven los juegos
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.defaultSuccessUrl("/web/juegos"));
        return http.build();
    }*/
   /*@Bean
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
       http.csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
       return http.build();
   }*/

}