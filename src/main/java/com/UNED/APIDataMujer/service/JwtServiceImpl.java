package com.UNED.APIDataMujer.service;

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

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService{

    @Value("${application.spring.security.secret-key}")
    private  String secretKey;
    @Value("${application.spring.security.expiration}")
    private long expiration;
    @Value("${application.spring.security.refresh-expiration}")
    private long refreshExpiration;


    @Override
    public String generateAccessToken(final User user) {
        return buildToken(user, expiration);
    }

    @Override
    public String generateRefreshToken(final User user) {
        return buildToken(user, refreshExpiration);
    }

    @Override
    public String getUsername(final String token) {
        return getPayload(token)
                .getSubject();
    }

    @Override
    public boolean isTokenValid(final String token, final User user) {
        var username = getUsername(token);
        return username.equals(user.getUsername()) || !isTokenExpired(getExpiration(token));
    }

    private String buildToken(final User user, long expiration){
        return Jwts.builder()
                .subject((user.getUsername()))
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+expiration))
                .signWith(getSecretKey())
                .compact();
    }

    private Claims getPayload(final String token){
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Date getExpiration(final String token){
        return getPayload(token)
                .getExpiration();
    }

    private boolean isTokenExpired(Date expiration) {
        return expiration.before((new Date()));
    }
}
