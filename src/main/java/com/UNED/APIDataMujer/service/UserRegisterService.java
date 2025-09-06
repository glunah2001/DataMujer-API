package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;

public interface UserRegisterService {
    PhysicalPersonDTO physicalRegister(PhysicalPersonRegisterDTO physicalRegisterDTO);
    LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO);
}
