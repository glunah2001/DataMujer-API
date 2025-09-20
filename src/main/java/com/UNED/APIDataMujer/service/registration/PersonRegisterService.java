package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.request.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonRegisterDTO;

public interface PersonRegisterService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO);
}
