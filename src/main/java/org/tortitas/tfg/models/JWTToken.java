package org.tortitas.tfg.models;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

@Component
public class JWTToken {

    private final RsaJsonWebKey rsaJsonWebKey;

    // La clave se genera UNA SOLA VEZ al arrancar Spring
    public JWTToken() throws JoseException {
        this.rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        this.rsaJsonWebKey.setKeyId("k1");
    }

    public String generateToken(String username, Rol rol) throws JoseException {
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Tortitas");
        claims.setAudience("Audiencia Nacional");
        claims.setExpirationTimeMinutesInTheFuture(60);
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject(username);
        claims.setClaim("rol", rol.name()); //para guardar el rol de usuario en el token
       // claims.setClaim("email", username); ¿esto pa que lo teniamos?

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(rsaJsonWebKey.getPrivateKey());
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return jws.getCompactSerialization();
    }

    //Nuevito -> para sacar el rol sin hacer toda la validacion again
    public String getRolFromToken(String token) throws Exception {
        JwtClaims claims = getClaims(token);
        return claims.getStringClaimValue("rol"); //la idea es que segun el rol, muestre lo de insertar juego o no en el controlador
    }


    // metodo privado aux para no repetir el codigo de validacion
    // aqui se explica mejor -> https://www.arquitecturajava.com/el-principio-dry-y-la-encapsulacion/
    private JwtClaims getClaims(String token) throws Exception {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("Tortitas")
                .setExpectedAudience("Audiencia Nacional")
                .setVerificationKey(rsaJsonWebKey.getPublicKey())
                .build();

        return jwtConsumer.processToClaims(token);
    }

    public boolean isTokenValid(String token) {
        try {
            //aqui solo he cambiado que en vez de ser  validateTokenAndGetUsername(token) sea el nuevo
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*

    URL DEL SITIO POR SI QUEREIS REVISAR: https://www.descope.com/learn/post/jwt-claims
    EXTRA QUIZA INTERESANTE: https://www.baeldung.com/spring-security-thymeleaf

    public String validateTokenAndGetUsername(String token) throws Exception {
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime()
                .setAllowedClockSkewInSeconds(30)
                .setRequireSubject()
                .setExpectedIssuer("Tortitas")
                .setExpectedAudience("Audiencia Nacional")
                .setVerificationKey(rsaJsonWebKey.getPublicKey())
                .build();

        return jwtConsumer.processToClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            validateTokenAndGetUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }*/
}
