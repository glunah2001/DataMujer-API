package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.User;

public interface PhysicalPersonService {
    PhysicalPersonDTO getPersonByNationalId(String nationalId);
    SimplePage<PhysicalPersonDTO> getPersonByName(String name, int page);
    SimplePage<PhysicalPersonDTO> getPersonBySurname(String surname, int page);
    PhysicalPersonDTO updateMyPhysicalProfile(User user,
                                              PhysicalPersonUpdateDTO dto);
    PhysicalPersonDTO toDto(User user);
}
