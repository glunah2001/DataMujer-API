package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.register.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import org.springframework.security.core.Authentication;

public interface UserService {
    Object getMyProfile(Authentication authentication);
    LegalPersonDTO updateMyLegalProfile(Authentication authentication, LegalPersonUpdateDTO dto);
    PhysicalPersonDTO updateMyPhysicalProfile(Authentication authentication, PhysicalPersonUpdateDTO dto);
}
