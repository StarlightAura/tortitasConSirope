package org.tortitas.tfg.config;

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;
import org.tortitas.tfg.models.Rol;

/**
 *Componente encargado de la generacion, firma y lectura de los tokens de seguridad (JWT).
 *<p>
 *Esta clase funciona como la central criptografica del proyecto. Genera un par de claves
 *asimetricas RSA de 2048 bits al arrancar para firmar las entradas (tokens)
 *de los usuarios y comprobar que nadie las haya manipulado.
 *</p>
 *@author StarlightAura
 *@author Laura Martín Martínez
 */
@Component
public class JWTToken {
    private final RsaJsonWebKey rsaJsonWebKey;

    /**
     *Constructor del componente. Configura el motor criptografico nada mas arrancar la app.
     *<p>
     *Genera la pareja de claves RSA con un tamaño seguro (2048 bits). Las claves se crean
     *directamente en la memoria RAM, por lo que solo existen mientras el servidor este encendido.
     *</p>
     *@throws JoseException Por si ocurre algun fallo con la libreria al generar las claves.
     */
    public JWTToken() throws JoseException {
        this.rsaJsonWebKey = RsaJwkGenerator.generateJwk(2048);
        this.rsaJsonWebKey.setKeyId("k1");
    }

    /**
     *Crea un token JWT empaquetando el nombre de usuario y su rol.
     *<p>
     *Rellena los datos de la sesion, inyecta el rol del usuario para que la interfaz web se adapte,
     *y firma el resultado usando la clave privada del servidor.
     *</p>
     *@param username El nombre de usuario que se ha logueado.
     *@param rol El rol (ADMIN o USER) que tiene asignado.
     *@return El token JWT en formato String.
     *@throws JoseException Por si falla el proceso de firma digital.
     */
    public String generateToken(String username, Rol rol) throws JoseException {
        //Definicion de los claims
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Tortitas"); //El creador del token (nuestra app)
        claims.setAudience("Audiencia Nacional"); //El destinatario (para quien es valido)
        claims.setExpirationTimeMinutesInTheFuture(60); //Caducidad del token (1 hora de sesion activa)
        claims.setGeneratedJwtId(); //ID unico para evitar que nos dupliquen el token
        claims.setIssuedAtToNow(); //Fecha y hora de creacion (ahora mismo)
        claims.setNotBeforeMinutesInThePast(2); //Margen de 2 minutos
        claims.setSubject(username); //El dueño de este token
        claims.setClaim("rol", rol.name()); //Guardamos el rol para poder leerlo luego en las vistas

        //Proceso de firma
        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson()); //Pasamos los datos anteriores a formato JSON
        jws.setKey(rsaJsonWebKey.getPrivateKey()); //El token se firma usando la clave privada
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256); //Usamos el algoritmo RS256

        //Lo empaquetamos en el string definitivo codificado en Base64
        return jws.getCompactSerialization();
    }
    /**
     *Comprueba si un token recibido es totalmente valido y seguro.
     *@param token El string del token que envia el cliente.
     *@return true si la firma es correcta y no ha caducado, o false si ha sido manipulado o esta caducado.
     */
    public boolean isTokenValid(String token) {
        try {
            buildConsumer().processToClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     *Extrae el rol que guardamos dentro del token.
     *@param token El string del token validado.
     *@return El texto del rol ("ADMIN" o "USER") para pintar o no los paneles de control.
     *@throws Exception Si el token esta roto o no tiene el campo "rol".
     */
    public String getRol(String token) throws Exception {
        return buildConsumer().processToClaims(token).getStringClaimValue("rol");
    }

    /**
     *Metodo auxiliar para configurar las reglas de validacion de los tokens.
     *<p>
     *Aqui definimos que cosas le vamos a exigir a un token para darlo por bueno,
     *usando siempre la clave publica para verificar la autenticidad de la firma.
     *</p>
     *@return Un objeto {@link JwtConsumer} listo para validar tokens bajo nuestras normas.
     */
    private JwtConsumer buildConsumer() {
        return new JwtConsumerBuilder()
                .setRequireExpirationTime() //Exigimos que tenga fecha de caducidad
                .setAllowedClockSkewInSeconds(30) //Margen de desfase de 30 segundos
                .setRequireSubject() //Exigimos que tenga un usuario asociado
                .setExpectedIssuer("Tortitas") //Solo aceptamos tokens emitidos por nosotros
                .setExpectedAudience("Audiencia Nacional") //Solo aceptamos tokens creados para nuestra app
                .setVerificationKey(rsaJsonWebKey.getPublicKey()) //Verificamos de forma segura usando la clave publica
                .build();
    }
}
