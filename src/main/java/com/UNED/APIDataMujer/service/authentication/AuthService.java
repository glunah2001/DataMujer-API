package com.UNED.APIDataMujer.service.authentication;

import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;

public interface AuthService {
    TokenResponse login (UserLoginDTO userLoginDTO);
    TokenResponse refresh (String authHeader);
}
