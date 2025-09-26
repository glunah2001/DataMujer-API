package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.entity.LegalPerson;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.repository.LegalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Clase de servicio exclusiva de los usuarios que son personas jurídicas.
 * @author glunah2001
 * @see LegalPersonService
 * */
@Service
@RequiredArgsConstructor
public class LegalPersonServiceImpl implements LegalPersonService{

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;
    private final PersonMapper personMapper;

    /**
     * Función de interfaz. Se encarga de retornar un DTO con base en su identificación jurídica.
     * @param legalId cédula jurídica.
     * @return Dto. Del usuario jurídico.
     * @throws ResourceNotFoundException en caso de que la persona con dicha identidad no exista.
     * */
    @Override
    public LegalPersonDTO getPersonByLegalId(String legalId) {
        var lp = legalPersonRepository.findByLegalId(legalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La persona con cédula legal: "+legalId+
                                "no se encuentra registrada.")
                );

        var user = getUserByPerson(lp.getPerson());
        return personMapper.toDto(user, lp);
    }

    /**
     * Función de interfaz encargada de obtener una paginación de personas jurídicas según su
     * nombre de negocio.
     * @param name nombre del negocio.
     * @param page indicador de paginación.
     * @return paginación de las personas jurídicas según el nombre indicado.
     * */
    @Override
    public SimplePage<LegalPersonDTO> getPersonByBusinessName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var businessNameSearch = legalPersonRepository.findByBusinessNameContainingIgnoreCase(name, pageable);

        return PaginationUtil.wrapInPage(businessNameSearch,
                lp -> personMapper.toDto(
                        getUserByPerson(lp.getPerson()), lp));
    }

    /**
     * Función de interfaz encargada de actualizar los datos propios de la tabla de personas
     * jurídicas en la BD.
     * @param user usuario recuperado de la bd.
     * @param dto información nueva a actualizar.
     * @return Dto. De la persona con la nueva información.
     * */
    @Override
    @Transactional
    public LegalPersonDTO updateMyLegalProfile(User user, LegalPersonUpdateDTO dto) {
        var legalPerson = getLegalPerson(user);

        legalPerson.setBusinessName(dto.businessName());
        legalPerson.setFoundationDate(dto.foundationDate());
        var updatedLegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, updatedLegalPerson);
    }

    /**
     * Función de interfaz encargada de obtener la información de la persona y retornarla
     * en un dto.
     * @param user usuario de la persona.
     * @return Dto. De persona Jurídica.
     * */
    @Override
    public LegalPersonDTO toDto(User user) {
        LegalPerson person = getLegalPerson(user);
        return personMapper.toDto(user, person);
    }

    /**
     * Función auxiliar encargada de obtener una persona Legal del repositorio según él,
     * id de persona abstracta.
     * @param user usuario de la persona.
     * @return entidad de la PersonaLegal recuperada de la bd.
     * @throws ResourceNotFoundException en caso de que la persona legal no pueda ser recuperado.
     * */
    private LegalPerson getLegalPerson(User user){
        return legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No es posible recuperar información " +
                                "de su persona.")
                );
    }

    /**
     * Función auxiliar encargada de recuperar el usuario de la persona según su persona abstracta
     * asociada.
     * @param person persona abstracta.
     * @return User de la persona asociada.
     * @throws ResourceNotFoundException en caso de que no se encuentre al usuario asociado.
     * */
    private User getUserByPerson(Person person){
        return userRepository.findByPerson(person)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No es posible recuperar información " +
                                "de su persona.")
                );
    }
}
