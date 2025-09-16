package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final TokenMapper tokenMapper;

    /**
     * Se encarga de revocar el token indicado.
     * Esta función no hace rollback ante una excepción controlada.
     * @param token token utilizado para la operación.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeToken(Token token) {
        token.setRevoked(true);
        token.setExpired(true);
        tokenRepository.save(token);
    }

    /**
     * Revoca todos los tokens válidos de un usuario
     * @param user usuario del que se revocarán todos los tokens
     */
    @Transactional
    public void revokeAllActiveTokens(User user) {
        List<Token> tokens = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUserId(user.getId());

        if (tokens.isEmpty()) return;

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(tokens);
    }

    /**
     * Genera un token con UUID + expiración en milisegundos
     * @param expirationMillis tiempo en milisegundos para que expire el token
     * @return token generado en formato "UUID_expiration"
     */
    public String generateToken(long expirationMillis) {
        long expiration = Instant.now().toEpochMilli() + expirationMillis;
        return UUID.randomUUID().toString() + "_" + expiration;
    }

    /**
     * Persistir un token en la base de datos
     * @param tokenValue valor del token (JWT o UUID+expiración)
     * @param user usuario al que pertenece el token
     * @param type tipo de token (BEARER, ACTIVATION, PASSWORD_RESET)
     */
    public void saveToken(String tokenValue, User user, TokenType type) {
        Token token = tokenMapper.toEntity(tokenValue, user, type);
        tokenRepository.save(token);
    }

    /**
     * Comprueba si un token está expirado según su valor (UUID_expiration)
     * @param token token a verificar
     * @return true si está expirado
     */
    public boolean isTokenExpired(Token token) {
        String[] parts = token.getToken().split("_");
        long expiration = Long.parseLong(parts[1]);
        return Instant.now().toEpochMilli() > expiration;
    }
}
