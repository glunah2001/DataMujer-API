package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.response.ProfileDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;


public interface UserService {
    ProfileDTO findMyProfile(Authentication authentication);
    ProfileDTO findPersonByUsername(String username);

    User findMyUser(Authentication authentication);
    User findUserByUsername(String username);

    LegalPersonDTO updateMyLegalProfile(Authentication authentication,
                                        LegalPersonUpdateDTO dto);
    PhysicalPersonDTO updateMyPhysicalProfile(Authentication authentication,
                                              PhysicalPersonUpdateDTO dto);

    PhysicalPersonDTO findPersonByNationalId(String nationalId);
    LegalPersonDTO findPersonByLegalId(String legalId);

    SimplePage<PhysicalPersonDTO> findPersonByName(String name, int page);
    SimplePage<LegalPersonDTO> findPersonByBusinessName(String name, int page);
    SimplePage<PhysicalPersonDTO> findPersonBySurname(String surname, int page);
}
