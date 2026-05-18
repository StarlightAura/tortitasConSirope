package org.tortitas.tfg.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 *Componente de seguridad que actua como un filtro de interceptacion personalizado.
 *<p>
 *Se encarga de auditar cada peticion HTTP entrante para extraer, validar y establecer
 *el contexto de autenticacion basado en JSON Web Tokens (JWT). Hereda de
 *{@link OncePerRequestFilter} para asegurar que la logica de validacion se ejecute
 *estrictamente una unica vez por cada ciclo de vida de la peticion del cliente.
 *</p>
 *@author Laura Martín Martínez
 *@author StarlightAura
 *@see OncePerRequestFilter
 *@see SecurityContextHolder
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    /**Logger para pintar en la consola del servidor los avisos o fallos que ocurran.*/
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired private JWTToken jwtToken;

    /**
     *Metdo principal del filtro que intercepta la peticion HTTP entrante.
     *<p>
     *Busca el token JWT tanto en las cabeceras (si viene de una peticion API REST)
     *como en la sesion del servidor (si el usuario esta navegando por las vistas web de Thymeleaf).
     *Si encuentra un token valido, extrae su rol, le asigna sus permisos en Spring Security
     *y le da permiso para entrar a las rutas protegidas.
     *</p>
     *@param request Objeto con todos los datos de la peticion del cliente.
     *@param response Objeto para gestionar la respuesta o redirigir en caso de error.
     *@param filterChain Cadena de filtros de Spring a la que delegamos el control cuando terminamos.
     *@throws ServletException Por si ocurre algun fallo interno en el contenedor de Servlets.
     *@throws IOException Por si hay problemas leyendo o escribiendo datos en la peticion.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        //Intentamos sacar el token si viene como una peticion API REST (es decir, desde la cabecera)
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); //Quitamos la palabra "Bearer " para quedarnos solo con el texto del token
        }

        //Si no venia en la cabecera, lo buscamos en la sesion web como plan B (es decir, para Thymeleaf)
        if (token == null) {
            HttpSession session = request.getSession(false); //Ponemos false para no crear una sesion nueva si no existe
            if (session != null) {
                token = (String) session.getAttribute("token");
            }
        }

        //Proceso de validacion y logueo interno
        if (token != null && jwtToken.isTokenValid(token)) {
            try {
                //Sacamos el rol que guardamos dentro del token (el payload)
                String rol = jwtToken.getRol(token);
                //Creamos el objeto de autenticacion con el rol y los permisos correspondientes
                //Mapeamos ese rol a un formato que entienda Spring Security (añadiendo el prefijo ROLE_ ya que Spring no entiende Rol como tal)
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(rol,null, List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                //Guardamos al usuario en el contexto de seguridad para este hilo de ejecucion.
                //A partir de aqui, la app ya sabe quien es y que puede hacer.
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                log.error("Error al procesar los claims del JWT Token: {}", e.getMessage());
                //Por seguridad, si algo falla a mitad del proceso, limpiamos cualquier rastro de sesion
                SecurityContextHolder.clearContext();
            }
        } else {
            //Si el token no existe, esta caducado o esta manipulado, vaciamos el contexto para denegar el acceso
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
