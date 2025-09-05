package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.mapper.UserMapper;
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
    private final PersonRepository personRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PhysicalPersonDTO physicalRegister(final PhysicalPersonRegisterDTO physicalRegisterDTO) {
        CommonRegisterDTO commonDto = physicalRegisterDTO.commonRegisterDTO();

        final var person = commonRegister(commonDto);

        var physicalPerson = personMapper.toEntity(person, physicalRegisterDTO);
        PhysicalPerson registeredPhysicalPerson = physicalPersonRepository.save(physicalPerson);
        User user = userRepository.findByUsername(commonDto.username())
                .orElseThrow();

        return personMapper.toDto(person, user, registeredPhysicalPerson);
    }

    @Override
    public TokenResponse login(UserLoginDTO userLoginDTO) {
        return null;
    }

    @Override
    public TokenResponse refresh(String authHeader) {
        return null;
    }

    private Person commonRegister(final CommonRegisterDTO commonDto){
        var person = personMapper.ToEntity(commonDto);
        Person registeredPerson = personRepository.save(person);

        var user = userMapper.toEntity(registeredPerson, commonDto);
        User registeedUser = userRepository.save(user);

        return registeredPerson;
    }
}
