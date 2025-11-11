package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper para los tokens a entidades a registrar en la bd.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class TokenMapper {

    /**
     * Función que mapea datos manualmente a una entidad token a registrar.
     * @param jjwt cadena de token.
     * @param user usuario al que pertenece el token.
     * @param type tipo de token del que se trata.
     * */
    public Token toEntity(String jjwt, User user, TokenType type){
        return Token.builder()
                .token(jjwt)
                .tokenType(type)
                .revoked(false)
                .expired(false)
                .user(user)
                .build();
    }
}
