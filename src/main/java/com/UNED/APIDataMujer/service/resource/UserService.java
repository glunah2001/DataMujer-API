package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    Object getMyProfile(Authentication authentication);
    User getMyUser(Authentication authentication);

    LegalPersonDTO updateMyLegalProfile(Authentication authentication, LegalPersonUpdateDTO dto);
    PhysicalPersonDTO updateMyPhysicalProfile(Authentication authentication, PhysicalPersonUpdateDTO dto);

    Object findByUsername(String username);
    PhysicalPersonDTO findByNationalId(String nationalId);
    LegalPersonDTO findByLegalId(String legalId);

    List<PhysicalPersonDTO> findByName(String name);
    List<LegalPersonDTO> findByBusinessName(String name);
    List<PhysicalPersonDTO> findBySurname(String surname);
}
