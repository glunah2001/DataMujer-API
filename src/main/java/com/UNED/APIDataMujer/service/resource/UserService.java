package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    Object getMyProfile(Authentication authentication);
    User getMyUser(Authentication authentication);
    LegalPersonDTO updateMyLegalProfile(Authentication authentication, LegalPersonUpdateDTO dto);
    PhysicalPersonDTO updateMyPhysicalProfile(Authentication authentication, PhysicalPersonUpdateDTO dto);
}
