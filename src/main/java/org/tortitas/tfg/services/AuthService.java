package org.tortitas.tfg.services;

import lombok.RequiredArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.tortitas.tfg.config.JWTToken;
import org.tortitas.tfg.config.Token;
import org.tortitas.tfg.exception.UserNotFoundException;
import org.tortitas.tfg.models.User;
import org.tortitas.tfg.record.LoginRequest;
import org.tortitas.tfg.record.RegisterRequest;
import org.tortitas.tfg.record.TokenResponse;
import org.tortitas.tfg.repositories.TokenRepository;
import org.tortitas.tfg.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTToken jwtToken;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public TokenResponse register(RegisterRequest request) throws JoseException {

        if (userRepository.findById(request.name()).isPresent()){
            throw new IllegalStateException("User already exist");
        }

        User user = User.builder()
                .name(request.name())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User saveUser = userRepository.save(user);

        var accessToken = jwtToken.generateToken(user.getName(), user.getRole());

        revokeAllUserTokens(saveUser);
        saveUserToken(saveUser, accessToken);

        return new TokenResponse(accessToken);
    }

    public TokenResponse login (LoginRequest request) throws JoseException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.name(),
                        request.password()
                )
        );

        var user = userRepository.findById(request.name())
                .orElseThrow(() -> new UserNotFoundException("User not found."));

        var accessToken = jwtToken.generateToken(user.getName(), user.getRole());

        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);
        return new TokenResponse(accessToken);
    }

    private void saveUserToken(User user, String jwtToken){
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();

        final var saveToken = tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user){
        var validTokens = tokenRepository.findAllValidTokensByUser(user.getName());

        if (validTokens.isEmpty())return;

        validTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });

        tokenRepository.saveAll(validTokens);

    }

    //TODO PUEDO CREAR UN REFRESH TOKEN

}
