package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.register.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonRegisterDTO;

public interface UserRegisterService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO);
}
