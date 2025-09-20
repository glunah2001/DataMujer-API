package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.request.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonRegisterDTO;
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

/**
 * Clase encargada exclusivamente de llevar a cabo el registro de nuevas personas
 * como usuarios del sistema.
 * @author glunah2001
 * @see PersonRegisterService
 * */
@Service
@RequiredArgsConstructor
public class PersonRegisterServiceImpl implements PersonRegisterService {

    private final LegalPersonRepository legalPersonRepository;
    private final PersonRepository personRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final UserRepository userRepository;

    private final PersonMapper personMapper;
    private final UserMapper userMapper;

    private final ActivationService activationService;

    private record CommonRegisterResult(User user, Person person){};

    /**
     * Función de interfaz encargada del registro de los datos propios de una persona jurídica.
     * @param legalPersonRegisterDTO dto. Con información propia de una persona jurídica.
     * @return información personal pública del usuario registrado exitósamente (pendiente de activación).
     * */
    @Override
    @Transactional
    public LegalPersonDTO legalRegister(final LegalPersonRegisterDTO legalPersonRegisterDTO) {
        CommonRegisterDTO commonDto = legalPersonRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.LEGAL);
        Person person = personData.person;
        User user = personData.user;

        var legalPerson = personMapper.toEntity(person, legalPersonRegisterDTO);
        LegalPerson registeredlegalPerson = legalPersonRepository.save(legalPerson);

        activationService.generateActivationToken(user);

        return personMapper.toDto(user, registeredlegalPerson);
    }

    /**
     * Función de interfaz encargada del registro de los datos propios de una persona física.
     * @param physicalRegisterDTO dto. Con información propia de una persona física.
     * @return información personal pública del usuario registrado exitósamente (pendiente de activación).
     * */
    @Override
    @Transactional
    public PhysicalPersonDTO physicalRegister(final PhysicalPersonRegisterDTO physicalRegisterDTO) {
        CommonRegisterDTO commonDto = physicalRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.FISICA);
        Person person = personData.person;
        User user = personData.user;

        var physicalPerson = personMapper.toEntity(person, physicalRegisterDTO);
        PhysicalPerson registeredPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        activationService.generateActivationToken(user);

        return personMapper.toDto(user, registeredPhysicalPerson);
    }

    /**
     * Función auxiliar encargada del registro de los datos comunes (usuario y persona abstracta).
     * @param commonDto datos comunes almacenados en un dto.
     * @param personType distintivo para el tipo de persona que se está manejando en este caso.
     * @return record privado que contiene al usuario registrado y su persona en abstracto.
     * */
    private CommonRegisterResult commonRegister(final CommonRegisterDTO commonDto, PersonType personType){
        var person = personMapper.toEntity(commonDto, personType);
        Person registeredPerson = personRepository.save(person);

        var user = userMapper.toEntity(registeredPerson, commonDto);
        User registeredUser = userRepository.save(user);

        return new CommonRegisterResult(registeredUser, registeredPerson);
    }
}
