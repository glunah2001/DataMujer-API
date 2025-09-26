package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.CommonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.ProfileDTO;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase encargada de la consulta y modificación de información de los usuarios registrados.
 * @author  glunah2001, AHKolodin
 * @see UserService
 * */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final PhysicalPersonService physicalPersonService;
    private final LegalPersonService legalPersonService;
    private final UserRepository userRepository;

    /**
     * Función de interfaz que obtiene la información del perfil propio del usuario.
     * @param authentication credenciales de autentificación del usuario con de la que
     *                       se extrae el username.
     * @return el dto. Con la información no comprometedora completa de la persona física o legal
     * */
    @Override
    public ProfileDTO getMyProfile(final Authentication authentication) {
        var user = getMyUser(authentication);
        return mapUserToProfileDTO(user);
    }

    @Override
    public ProfileDTO getPersonByUsername(String username) {
        var user = getUserByUsername(username);
        return mapUserToProfileDTO(user);
    }

    /**
     * Función de interfaz que obtiene la entidad User del propio usuario.
     * @param authentication credenciales de autentificación del usuario.
     * @return entidad User de la BD.
     * */
    @Override
    public User getMyUser(final Authentication authentication) {
        return getUserByUsername(authentication.getName());
    }

    /**
     * Función auxiliar que extrae el Usuario de la BD mediante su ID
     * @param username que identifica al usuario.
     * @return el usuario sacado de la BD.
     * */
    @Override
    public User getUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se ha encontrado ningún usuario registrado con el " +
                                "nombre de usuario: "+username+".")
                );
    }

    /**
     * Función auxiliar que actualiza los datos comunes del usuario (usuario y persona abstracta)
     * @param authentication credenciales del usuario.
     * @param dto Dto. Que contiene todos los datos comunes del usuario.
     * @return User con sus datos actualizados
     * */
    @Override
    @Transactional
    public User updateUserData(Authentication authentication, CommonUpdateDTO dto){
        final var myUser = getMyUser(authentication);
        final var person = myUser.getPerson();

        person.setPhoneNumber(dto.phoneNumber());
        person.setCountry(dto.country());
        person.setLocation(dto.location());
        myUser.setEmail(dto.email());

        return userRepository.save(myUser);
    }

    private ProfileDTO mapUserToProfileDTO(User user){
        return switch(user.getPerson().getPersonType()){
            case FISICA -> physicalPersonService.toDto(user);
            case LEGAL -> legalPersonService.toDto(user);
        };
    }

}
