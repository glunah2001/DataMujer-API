package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.register.CommonUpdateDTO;
import com.UNED.APIDataMujer.dto.register.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.repository.LegalPersonRepository;
import com.UNED.APIDataMujer.repository.PersonRepository;
import com.UNED.APIDataMujer.repository.PhysicalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    @Override
    public Object getMyProfile(final Authentication authentication) {
        String username = authentication.getName();
        var user = getUserByUsername(username);

        return switch(user.getPerson().getPersonType()){
            case FISICA -> getPhysicalProfile(user);
            case LEGAL -> getLegalProfile(user);
        };
    }

    @Override
    @Transactional
    public LegalPersonDTO updateMyLegalProfile(final Authentication authentication,
                                               final LegalPersonUpdateDTO dto) {
        String username = authentication.getName();
        var user = updateCommonData(username, dto.commonUpdateDTO());

        var legalPerson = legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        legalPerson.setBusinessName(dto.businessName());
        legalPerson.setFoundationDate(dto.foundationDate());
        var updatedLegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, updatedLegalPerson);
    }

    @Override
    @Transactional
    public PhysicalPersonDTO updateMyPhysicalProfile(final Authentication authentication,
                                                     final PhysicalPersonUpdateDTO dto) {
        String username = authentication.getName();
        var user = updateCommonData(username, dto.commonUpdateDTO());

        var physicalPerson = physicalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        physicalPerson.setName(dto.name());
        physicalPerson.setFirstSurname(dto.firstSurname());
        physicalPerson.setSecondSurname(dto.secondSurname());
        physicalPerson.setProfession(dto.profession());
        physicalPerson.setBirthDate(dto.birthDate());
        var updatedPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        return personMapper.toDto(user, updatedPhysicalPerson);
    }





    private User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario "+username+" no se encuentra registrado."));
    }

    private User updateCommonData(String username, CommonUpdateDTO dto){
        var user = getUserByUsername(username);
        var person = user.getPerson();

        person.setPhoneNumber(dto.phoneNumber());
        person.setCountry(dto.country());
        person.setLocation(dto.location());
        user.setEmail(dto.email());


        var updatePerson = personRepository.save(person);
        return userRepository.save(user);
    }

    private PhysicalPersonDTO getPhysicalProfile(User user){
        var person = physicalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        return personMapper.toDto(user, person);
    }

    private LegalPersonDTO getLegalProfile(User user){
        var person = legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        return personMapper.toDto(user, person);
    }
}
