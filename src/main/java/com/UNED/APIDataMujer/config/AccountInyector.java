package com.UNED.APIDataMujer.config;

import com.UNED.APIDataMujer.dto.request.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.enums.Country;
import com.UNED.APIDataMujer.enums.PersonType;
import com.UNED.APIDataMujer.enums.Role;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.mapper.UserMapper;
import com.UNED.APIDataMujer.repository.LegalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.registration.PersonRegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AccountInyector implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;

    private final PersonMapper personMapper;
    private final UserMapper userMapper;

    private final PersonRegisterService personRegisterService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if(userRepository.findByUsername("DataMujer2022").isPresent())
            return;

        var commonData = new CommonRegisterDTO("DataMujer2022",
                "asociación@datamujer.com",
                "D@ta_Muj3e_22",
                "+506 87552860",
                Country.COSTA_RICA,
                "Curridabat, San José");
        var legalData = new LegalPersonRegisterDTO(commonData,
                "3002857115",
                "Asociación Data Mujer",
                LocalDate.of(2022, 1, 1));

        var person = personMapper.toEntity(commonData, PersonType.LEGAL);
        var insertUser = userMapper.toEntity(person, commonData);
        insertUser.setRole(Role.ROLE_ADMIN);
        insertUser.setActive(true);

        var user = userRepository.save(insertUser);

        var legal = personMapper.toEntity(user.getPerson(), legalData);

        legalPersonRepository.save(legal);
        legalPersonRepository.flush();
    }
}
