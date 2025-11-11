package com.UNED.APIDataMujer.service.jwt;

import com.UNED.APIDataMujer.entity.User;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    String getUsername(String token);
    boolean isTokenValid(String token, User user);
}
