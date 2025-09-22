package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    Object finMyProfile(Authentication authentication);
    User findMyUser(Authentication authentication);
    User findUserByUsername(String username);

    LegalPersonDTO updateMyLegalProfile(Authentication authentication,
                                        LegalPersonUpdateDTO dto);
    PhysicalPersonDTO updateMyPhysicalProfile(Authentication authentication,
                                              PhysicalPersonUpdateDTO dto);

    Object findPersonByUsername(String username);
    PhysicalPersonDTO findPersonByNationalId(String nationalId);
    LegalPersonDTO findPersonByLegalId(String legalId);

    List<PhysicalPersonDTO> findPersonByName(String name);
    List<LegalPersonDTO> findPersonByBusinessName(String name);
    List<PhysicalPersonDTO> findPersonBySurname(String surname);
}
