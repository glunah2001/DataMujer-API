package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.CommonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.response.ProfileDTO;
import com.UNED.APIDataMujer.entity.LegalPerson;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.repository.LegalPersonRepository;
import com.UNED.APIDataMujer.repository.PhysicalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Clase encargada de la consulta y modificación de información de los usuarios registrados.
 * @author  glunah2001, AHKolodin
 * @see UserService
 * */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final PersonMapper personMapper;

    /**
     * Función de interfaz que obtiene la información del perfil propio del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     *                       se extrae el username.
     * @return el dto. Con la información no comprometedora completa de la persona física o legal
     * */
    @Override
    public ProfileDTO findMyProfile(final Authentication authentication) {
        var user = findMyUser(authentication);
        return mapUserToProfileDTO(user);
    }

    @Override
    public ProfileDTO findPersonByUsername(String username) {
        var user = getUserByUsername(username);
        return mapUserToProfileDTO(user);
    }

    /**
     * Función de interfaz que obtiene la entidad User del propio usuario.
     * @param authentication credenciales de autentificación del usuario.
     * @return entidad User de la BD.
     * */
    @Override
    public User findMyUser(final Authentication authentication) {
        return getUserByUsername(authentication.getName());
    }

    @Override
    public User findUserByUsername(String username) {
        return getUserByUsername(username);
    }

    /**
     * Función de interfaz que permite actualizar la información propia del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     *                       se extrae el username.
     * @param dto Dto. Con la nueva información que el usuario (persona legal) desea
     *            actualizar.
     * @return el dto. Con la información no comprometedora completa de la persona legal
     * @throws IllegalArgumentException en caso de que la persona legal no se encuentre en la bd.
     * */
    @Override
    @Transactional
    public LegalPersonDTO updateMyLegalProfile(final Authentication authentication,
                                               final LegalPersonUpdateDTO dto) {
        var user = updateCommonData(authentication, dto.commonUpdateDTO());
        var legalPerson = getLegalPerson(user);

        legalPerson.setBusinessName(dto.businessName());
        legalPerson.setFoundationDate(dto.foundationDate());
        var updatedLegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, updatedLegalPerson);
    }

    /**
     * Función de interfaz que permite actualizar la información propia del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     * se extrae el username.
     * @param dto Dto. Con la nueva información que el usuario (persona física)
     *            desea actualizar.
     * @return el dto con la información no comprometedora completa de la persona física
     * @throws IllegalArgumentException en caso de que la persona física no se encuentre en la bd.
     * */
    @Override
    @Transactional
    public PhysicalPersonDTO updateMyPhysicalProfile(final Authentication authentication,
                                                     final PhysicalPersonUpdateDTO dto) {
        var user = updateCommonData(authentication, dto.commonUpdateDTO());
        var physicalPerson = getPhysicalPerson(user);

        physicalPerson.setName(dto.name());
        physicalPerson.setFirstSurname(dto.firstSurname());
        physicalPerson.setSecondSurname(dto.secondSurname());
        physicalPerson.setProfession(dto.profession());
        physicalPerson.setBirthDate(dto.birthDate());
        var updatedPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        return personMapper.toDto(user, updatedPhysicalPerson);
    }

    @Override
    public PhysicalPersonDTO findPersonByNationalId(String nationalId) {
        var pp = physicalPersonRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException("La persona física con " +
                        "identificación "+nationalId+" no fue encontrada."));
        return personMapper.toDto(getUserByPerson(pp.getPerson()), pp);
    }

    @Override
    public LegalPersonDTO findPersonByLegalId(String legalId) {
        var lp = legalPersonRepository.findByLegalId(legalId)
                .orElseThrow(() -> new IllegalArgumentException("La persona jurídica con " +
                        "identificación "+legalId+" no fue encontrada."));
        return personMapper.toDto(getUserByPerson(lp.getPerson()), lp);
    }

    @Override
    public SimplePage<PhysicalPersonDTO> findPersonByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var nameSearch = physicalPersonRepository.findByNameContainingIgnoreCase(name, pageable);
        return PaginationUtil.wrapInPage(nameSearch,
                pp -> personMapper.toDto(getUserByPerson(pp.getPerson()), pp));
    }

    @Override
    public SimplePage<LegalPersonDTO> findPersonByBusinessName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var businessNameSearch = legalPersonRepository.findByBusinessNameContainingIgnoreCase(name, pageable);
        return PaginationUtil.wrapInPage(businessNameSearch,
                lp -> personMapper.toDto(getUserByPerson(lp.getPerson()), lp));
    }

    @Override
    public SimplePage<PhysicalPersonDTO> findPersonBySurname(String surname, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var surnameSearch = physicalPersonRepository
                .findByAnySurnameContainingIgnoreCase(surname, pageable);
        return PaginationUtil.wrapInPage(surnameSearch,
                pp -> personMapper.toDto(getUserByPerson(pp.getPerson()), pp));
    }

    /**
     * Función auxiliar que extrae el Usuario de la BD mediante su ID
     * @param username que identifica al usuario.
     * @return el usuario sacado de la BD.
     * */
    private User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario "+username+" no se encuentra registrado."));
    }

    /**
     * Función auxiliar que actualiza los datos comunes del usuario (usuario y persona abstracta)
     * @param authentication credenciales del usuario.
     * @param dto Dto. Que contiene todos los datos comunes del usuario.
     * @return User con sus datos actualizados
     * */
    private User updateCommonData(Authentication authentication, CommonUpdateDTO dto){
        var user = findMyUser(authentication);
        var person = user.getPerson();

        person.setPhoneNumber(dto.phoneNumber());
        person.setCountry(dto.country());
        person.setLocation(dto.location());
        user.setEmail(dto.email());

        return userRepository.save(user);
    }

    private User getUserByPerson(Person person){
        return userRepository.findByPerson(person)
                .orElseThrow(() -> new
                        IllegalArgumentException("El usuario no se ha encontrado."));
    }

    private PhysicalPerson getPhysicalPerson(User user){
        return physicalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ocurrió un error al " +
                        "intentar obtener su información"));
    }

    private LegalPerson getLegalPerson(User user){
        return legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ocurrió un error al " +
                        "intentar obtener su información"));
    }

    private ProfileDTO mapUserToProfileDTO(User user){
        return switch(user.getPerson().getPersonType()){
            case FISICA -> personMapper.toDto(user, getPhysicalPerson(user));
            case LEGAL -> personMapper.toDto(user, getLegalPerson(user));
        };
    }

}
