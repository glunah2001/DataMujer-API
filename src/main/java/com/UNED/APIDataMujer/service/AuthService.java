package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.*;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;

public interface AuthService {
    TokenResponse login (UserLoginDTO userLoginDTO);
    TokenResponse refresh (String authHeader);
}
