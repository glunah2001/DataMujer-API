package com.UNED.APIDataMujer.service.jwt;

import com.UNED.APIDataMujer.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

/**
 * Clase que implementa la interfaz para la creación de los JSON Web Tokens.
 * @author glunah2001
 * @see JwtService
 * */
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{

    @Value("${application.spring.security.secret-key}")
    private  String secretKey;
    @Value("${application.spring.security.expiration}")
    private long expiration;
    @Value("${application.spring.security.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Función de interfaz que permite obtener desde otras clases el token de ACCESO.
     * @param user usuario al que pertenece el token.
     * @return un JWT (en string) con payload y firmado.
     * */
    @Override
    public String generateAccessToken(final User user) {
        return buildToken(user, expiration);
    }

    /**
     * Función de interfaz que permite obtener desde otras clases el token de REFRESCO.
     * @param user usuario al que pertenece el token.
     * @return un JWT (en string) con payload y firmado.
     * */
    @Override
    public String generateRefreshToken(final User user) {
        return buildToken(user, refreshExpiration);
    }

    /**
     * Función de interfaz que permite obtener el username del propietario desde un token.
     * @param token se trata del JWT recibido.
     * @return Username del usuario sacado desde el JWT.
     * */
    @Override
    public String getUsername(final String token) {
        return getPayload(token)
                .getSubject();
    }

    /**
     * Función de interfaz encargada de verificar que un JWT creado no esté expirado
     * según y que pertenezca al usuario que lo porta.
     * @param token JWT recibido de la que se examina la fecha de expiración del payload.
     * @param user usuario que porta el token.
     * @return booleano que indica si el token es o no es válido.
     * */
    @Override
    public boolean isTokenValid(final String token, final User user) {
        var username = getUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(getExpiration(token));
    }

    /**
     * Función principal encargada de crear JWT.
     * @param user usuario que será dueño del token en cuestión.
     * @param expiration expiración con la que contará el token.
     * @return retorna un JWT firmado y con su respectivo payload.
     * */
    private String buildToken(final User user, long expiration){
        return Jwts.builder()
                .subject((user.getUsername()))
                .id(UUID.randomUUID().toString())
                .claim("role", user.getRole().name())
                .claim("personType", user.getPerson().getPersonType().name())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Función auxiliar. Se encarga de separar y hacer legible su payload.
     * @param token JWT al que se le extraerá el payload
     * @return objeto Claims que contiene el payload del token.
     * */
    private Claims getPayload(final String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Función auxiliar. Se encarga de dar la llave secreta con la que se
     * firman los JWT. BAJO NINGÚN MOTIVO REVELAR LLAVE SECRETA.
     * @return llave secreta utilizada para la firma de tokens.
     * */
    private SecretKey getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Función auxiliar. Basándonos en el payload, extrae la fecha de expiración
     * del JWT.
     * @param token JWT del cual extraer la fecha.
     * @return fecha de expiración del JWT.
     * */
    private Date getExpiration(final String token){
        return getPayload(token)
                .getExpiration();
    }

    /**
     * Función auxiliar. Con base en la fecha de expiración calcula si el token
     * está o no está caducado.
     * @param expiration fecha de expiración sacada del JWT.
     * @return booleano. Está o no está expirado.
     * */
    private boolean isTokenExpired(Date expiration) {
        return expiration.before((new Date()));
    }
}
