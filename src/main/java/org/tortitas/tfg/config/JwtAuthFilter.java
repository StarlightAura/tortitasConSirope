package org.tortitas.tfg.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.tortitas.tfg.exception.JwtExceptionHandler;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.repositories.TokenRepository;
import org.tortitas.tfg.repositories.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTToken jwtToken;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final UserDetailsService userDetailsService;
    private final JwtExceptionHandler jwtExceptionHandler;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws IOException {

        try {

            if (request.getServletPath().contains("/auth")){
                filterChain.doFilter(request, response);
                return;
            }

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request, response);
                return;
            }

            final String jwtTokenFilter = authHeader.substring(7);
            final String userName =  jwtToken.getUserNameFromToken(jwtTokenFilter);

            if (userName == null || SecurityContextHolder.getContext().getAuthentication()!=null){
                filterChain.doFilter(request, response);
                return;
            }

            final Token token = tokenRepository.findByToken(jwtTokenFilter)
                    .orElse(null);

            if(token == null || token.isExpired()||token.isRevoked()){
                filterChain.doFilter(request, response);
                return;
            }

            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(userName);
            final Optional<User>user = userRepository.findById(userDetails.getUsername());

            if (user.isEmpty()){
                filterChain.doFilter(request, response);
                return;
            }

            final boolean isTokenValid = jwtToken.isTokenValid(jwtTokenFilter);
            if (!isTokenValid){
                filterChain.doFilter(request, response);
                return;
            }

            final var authToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            jwtExceptionHandler.handle(response,e);
        }


    }
}
