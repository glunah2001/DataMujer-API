package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.entity.User;

public interface LegalPersonService {
    LegalPersonDTO getPersonByLegalId(String legalId);
    SimplePage<LegalPersonDTO> getPersonByBusinessName(String name, int page);
    LegalPersonDTO updateMyLegalProfile(User user,
                                        LegalPersonUpdateDTO dto);
    LegalPersonDTO toDto(User user);
}
