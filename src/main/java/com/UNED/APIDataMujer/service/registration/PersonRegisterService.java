package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.register.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonRegisterDTO;

public interface PersonRegisterService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO);
}
