package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.*;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;

public interface AuthService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO);
    TokenResponse login (UserLoginDTO userLoginDTO);
    TokenResponse refresh (String authHeader);
}
