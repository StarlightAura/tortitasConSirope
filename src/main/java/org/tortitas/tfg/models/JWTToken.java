package org.tortitas.tfg.models;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

public class JWTToken {

    public static void jwtToken() throws JoseException {

        RsaJsonWebKey rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        rsaJsonWebKey.setKeyId("k1"); // key id
        JwtClaims claims = new JwtClaims(); // contenido del JWT

        claims.setIssuer("Tortitas"); // creador y firmador del token
        claims.setAudience("Audiencia Nacional"); // a quien se envia el token
        claims.setExpirationTimeMinutesInTheFuture(10); // tiempo de vida del token en minutos
        claims.setGeneratedJwtId(); // identificador unico del token
        claims.setIssuedAtToNow(); // cuando se crea el token (ahora mismo)
        claims.setNotBeforeMinutesInThePast(2); // tiempo anterior al que el token no es válido (2 minutos atrás)
        claims.setSubject("Sujeto");
        claims.setClaim("email", "tortitas@sirope.com");

        // un JWT es un JWS (JSON Web Signature) con JSON como payload
        JsonWebSignature jws = new JsonWebSignature();

        jws.setPayload(claims.toJson()); // el payload del JWS es el contenido JSON de JWT Claims
        jws.setKey(rsaJsonWebKey.getPrivateKey()); // se firma con la clave privada
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId()); // se pone la keyID
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256); // algoritmo de firma para proteger la integridad de JWT Claims

        // firma el JWS y produce serialización
        // con el formato
        // Header.Payload.Firma codificado en base64url
        // si se quiere encriptar se pone este JWT como payload de un objeto JsonWebEncryption
        // y se pone el cty (Content Type) a "jwt"
        String jwt = jws.getCompactSerialization();

        System.out.println(jwt);

    }

    public static void consumeJWT() throws JoseException {
/*
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // tiene que tener tiempo de vida
                .setAllowedClockSkewInSeconds(30)

 */
    }
}
