package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.*;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.entity.LegalPerson;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.PersonType;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.mapper.UserMapper;
import com.UNED.APIDataMujer.repository.LegalPersonRepository;
import com.UNED.APIDataMujer.repository.PersonRepository;
import com.UNED.APIDataMujer.repository.PhysicalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final PersonMapper personMapper;
    private final UserMapper userMapper;
    private final LegalPersonRepository legalPersonRepository;
    private final PersonRepository personRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final UserRepository userRepository;
    private record CommonRegisterResult(User user, Person person){};

    @Override
    @Transactional
    public LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO) {
        CommonRegisterDTO commonDto = legalPersonRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.LEGAL);
        Person person = personData.person;
        User user = personData.user;

        var legalPerson = personMapper.toEntity(person, legalPersonRegisterDTO);
        LegalPerson registeredlegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, registeredlegalPerson);
    }

    @Override
    @Transactional
    public PhysicalPersonDTO physicalRegister(final PhysicalPersonRegisterDTO physicalRegisterDTO) {
        CommonRegisterDTO commonDto = physicalRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.FISICA);
        Person person = personData.person;
        User user = personData.user;

        var physicalPerson = personMapper.toEntity(person, physicalRegisterDTO);
        PhysicalPerson registeredPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        return personMapper.toDto(user, registeredPhysicalPerson);
    }

    @Override
    public TokenResponse login(UserLoginDTO userLoginDTO) {
        return null;
    }

    @Override
    public TokenResponse refresh(String authHeader) {
        return null;
    }

    private CommonRegisterResult commonRegister(final CommonRegisterDTO commonDto, PersonType personType){
        var person = personMapper.toEntity(commonDto, personType);
        Person registeredPerson = personRepository.save(person);

        var user = userMapper.toEntity(registeredPerson, commonDto);
        User registeredUser = userRepository.save(user);

        return new CommonRegisterResult(registeredUser, registeredPerson);
    }
}
