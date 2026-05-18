package org.tortitas.tfg.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 *Clase central donde configuramos toda la seguridad de Spring Security.
 *<p>
 *Definimos los permisos de acceso a las rutas de la aplicacion, estableciendo que URLs
 *son publicas y cuales quedan restringidas segun el rol asignado al usuario.
 *</p>
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired JwtAuthFilter jwtAuthFilter;

    /**
     *Configura las reglas de acceso por URL y el comportamiento ante errores.
     *@param http Objeto para estructurar las reglas de seguridad.
     *@return La configuracion de seguridad del sistema ya construida.
     *@throws Exception Si ocurre un error en la configuracion al arrancar.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //Desactivamos la protección CSRF al gestionar la seguridad mediante tokens JWT.
                .csrf(csrf -> csrf.disable())
                //Deshabilitamos el formulario de inicio de sesión por defecto de Spring.
                .formLogin(form -> form.disable())
                .exceptionHandling(ex -> ex
                        // Control por si el usuario no esta logueado en absoluto (Falta el token)
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);// HTTP 401
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Token requerido o invalido\"}");
                            } else {
                                //Si es un usuario normal navegando por la web, lo mandamos al login
                                response.sendRedirect("/login");
                            }
                        })
                        //Control por si el usuario se ha logueado, pero intenta entrar a un sitio sin tener permisos
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);// HTTP 403
                                response.setContentType("application/json;charset=UTF-8");
                                response.getWriter().write("{\"error\": \"Forbidden\", \"message\": \"No tienes permisos de Admin\"}");
                            } else {
                                //Si pasa en la web, lo mandamos al home con un aviso de error por URL
                                response.sendRedirect("/home?error=unauthorized");
                            }
                        })
                )
                //Controles de acceso por url
                .authorizeHttpRequests(auth -> auth
                        //Recursos estaticos siempre permitidos
                        .requestMatchers("/css/**", "/img/**", "/js/**", "/favicon.ico").permitAll()
                        //Paginas publicas, para que cualquiera entrar a loguearse o registrarse
                        .requestMatchers("/", "/login", "/web/signin", "/web/signup").permitAll()
                        //API Publica con los endpoints para que la web o apps externas puedan hacer el login/registro
                        .requestMatchers("/api/auth/**").permitAll()

                        //API de Admin (CRUD). En general, lo que pueda ser modificar juegos (POST, PUT, DELETE) esta capado
                        .requestMatchers(HttpMethod.POST, "/api/products", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products", "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products", "/api/products/**").hasRole("ADMIN")

                        //API de Recomendaciones en el cual cualquier usuario que este autenticado puede pedir las recomendaciones
                        .requestMatchers("/api/recommendations", "/api/recommendations/**").authenticated()

                        //El manejador de IA esta protegido
                        .requestMatchers("/ai/**").authenticated()

                        //Solo el ADMIN puede ver las paginas web de insertar o gestionar productos
                        .requestMatchers("/web/products", "/web/products/**").hasRole("ADMIN")
                        //Cualquiera logueado puede entrar al home o a la parte de recomendaciones de Thymeleaf
                        .requestMatchers("/web/recommendations", "/web/recommendations/**", "/home").authenticated()

                        //Por ultimo, cualquier otra ruta que se nos haya olvidado configurar, va a pedir login (por si acaso)
                        .anyRequest().authenticated()
                )
                //Enganchamos nuestro filtro justo antes del filtro de login por defecto de Spring.
                //De esta forma atrapamos el token antes de que Spring intente buscar un usuario tradicional.
                .addFilterBefore(jwtAuthFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     *Configuramos el encriptador de contraseñas global de la aplicación.
     *<p>
     *Usamos el algoritmo hash BCrypt para guardar las contraseñas de forma totalmente
     *segura e irreversible en la base de datos.
     *</p>
     *@return El componente PasswordEncoder usando la estrategia BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}