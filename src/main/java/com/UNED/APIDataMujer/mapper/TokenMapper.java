package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenMapper {

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
