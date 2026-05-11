package org.tortitas.tfg.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.SignatureException;

@Component
public class JwtExceptionHandler extends RuntimeException {

    public void handle(HttpServletResponse response, Exception exception)throws IOException {
        response.setContentType("application/json");

        switch (exception){
            case ExpiredJwtException expiredJwtException -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                write(response, "Token expirado");
            }
            case MalformedJwtException malformedJwtException -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                write(response, "Token mal formado");
            }
            case SignatureException signatureException -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                write(response, "Firma inválida");
            }
            case InvalidTokenException invalidTokenException -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                write(response, exception.getMessage());
            }
            case null, default -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                write(response, "Token inválido");
            }
        }

    }

    private void write(HttpServletResponse response, String message) throws IOException{
        response.getWriter().write("""
                {"error:" "%s"}""".formatted(message));
    }


}
