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

/**
 * Clase encargada de la consulta y modificación de información de los usuarios registrados.
 * @author  glunah2001
 * @see UserService
 * */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final PersonRepository personRepository;

    private final PersonMapper personMapper;

    /**
     * Función de interfaz que obtiene la información del perfil propio del usuario..
     * @param authentication credenciales de autentificación del usuario con de la que
     * se extrae el username.
     * @return el dto con la información no comprometedora completa de la persona física o legal
     * */
    @Override
    public Object getMyProfile(final Authentication authentication) {
        String username = authentication.getName();
        var user = getUserByUsername(username);

        return switch(user.getPerson().getPersonType()){
            case FISICA -> getPhysicalProfile(user);
            case LEGAL -> getLegalProfile(user);
        };
    }

    /**
     * Función de interfaz que permite actualizar la información propia del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     * se extrae el username.
     * @param dto Dto con la nueva información que el usuario (persona legal) desea actualizar.
     * @return el dto con la información no comprometedora completa de la persona legal
     * @throws IllegalArgumentException en caso de que la persona legal no se encuentre en la bd.
     * */
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

    /**
     * Función de interfaz que permite actualizar la información propia del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     * se extrae el username.
     * @param dto Dto con la nueva información que el usuario (persona física) desea actualizar.
     * @return el dto con la información no comprometedora completa de la persona física
     * @throws IllegalArgumentException en caso de que la persona física no se encuentre en la bd.
     * */
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




    /**
     * Función auxiliar que extrae el Usuario de la BD mediante su ID
     * @param username que identifica al usuario.
     * @return el usuario extraido de la BD.
     * */
    private User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario "+username+" no se encuentra registrado."));
    }

    /**
     * Función auxiliar que actualiza los datos comunes del usuario (usuario y persona abstracta)
     * @param username que identifica al usuario.
     * @param dto Dto que contiene todos los datos comunes del usuario.
     * @return User con sus datos actualizados
     * */
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

    /**
     * Función auxiliar que recupera la información personal de un usuario que tiene por
     * propietario una persona física
     * @param user usuario recuperado de la BD ligado con la persona física a buscar.
     * @return retorna los datos de la persona física en un dto.
     * @throws IllegalArgumentException en caso que la persona física buscada no exista en la bd.
     * */
    private PhysicalPersonDTO getPhysicalProfile(User user){
        var person = physicalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        return personMapper.toDto(user, person);
    }

    /**
     * Función auxiliar que recupera la información personal de un usuario que tiene por
     * propietario una persona física
     * @param user usuario recuperado de la BD ligado con la persona jurídica a buscar.
     * @return retorna los datos de la persona jurídica en un dto.
     * @throws IllegalArgumentException en caso que la persona jurídica buscada no exista en la bd.
     * */
    private LegalPersonDTO getLegalProfile(User user){
        var person = legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Ocurrió un error al intentar obtener su información"));

        return personMapper.toDto(user, person);
    }
}
