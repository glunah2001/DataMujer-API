package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;

public interface AuthService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    TokenResponse login (UserLoginDTO userLoginDTO);
    TokenResponse refresh (String authHeader);
}
