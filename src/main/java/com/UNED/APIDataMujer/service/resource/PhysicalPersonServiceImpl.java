package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.repository.PhysicalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PhysicalPersonServiceImpl implements PhysicalPersonService{

    private final PhysicalPersonRepository physicalPersonRepository;
    private final UserRepository userRepository;
    private final PersonMapper personMapper;

    /**
     * Función de interfaz. Se encarga de retornar un DTO con base en su identificación nacional.
     * @param nationalId cédula nacional.
     * @return Dto. Del usuario físico.
     * @throws ResourceNotFoundException en caso de que la persona con dicha identidad no exista.
     * */
    @Override
    public PhysicalPersonDTO getPersonByNationalId(String nationalId) {
        var pp = physicalPersonRepository.findByNationalId(nationalId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La persona con cédula legal: "+nationalId+
                                "no se encuentra registrada.")
                );

        var user = getUserByPerson(pp.getPerson());
        return personMapper.toDto(user, pp);
    }

    /**
     * Función de interfaz encargada de obtener una paginación de personas nacionales según su
     * nombre.
     * @param name nombre de las personas.
     * @param page indicador de paginación.
     * @return paginación de las personas físicas según el nombre indicado.
     * */
    @Override
    public SimplePage<PhysicalPersonDTO> getPersonByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var nameSearch = physicalPersonRepository.findByNameContainingIgnoreCase(name, pageable);
        return PaginationUtil.wrapInPage(nameSearch,
                pp -> personMapper.toDto(
                        getUserByPerson(pp.getPerson()), pp));
    }

    /**
     * Función de interfaz encargada de obtener una paginación de personas nacionales según su
     * apellido.
     * @param surname apellido de las personas.
     * @param page indicador de paginación.
     * @return paginación de las personas físicas según el apellido indicado.
     * */
    @Override
    public SimplePage<PhysicalPersonDTO> getPersonBySurname(String surname, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var surnameSearch = physicalPersonRepository
                .findByAnySurnameContainingIgnoreCase(surname, pageable);
        return PaginationUtil.wrapInPage(surnameSearch,
                pp -> personMapper.toDto(
                        getUserByPerson(pp.getPerson()), pp));
    }

    /**
     * Función de interfaz encargada de actualizar los datos propios de la tabla de personas
     * nacionales en la BD.
     * @param user usuario recuperado de la bd.
     * @param dto información nueva a actualizar.
     * @return Dto. De la persona con la nueva información.
     * */
    @Override
    @Transactional
    public PhysicalPersonDTO updateMyPhysicalProfile(User user, PhysicalPersonUpdateDTO dto) {
        var physicalPerson = getPhysicalPerson(user);

        physicalPerson.setName(dto.name());
        physicalPerson.setFirstSurname(dto.firstSurname());
        physicalPerson.setSecondSurname(dto.secondSurname());
        physicalPerson.setProfession(dto.profession());
        physicalPerson.setBirthDate(dto.birthDate());
        var updatedPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        return personMapper.toDto(user, updatedPhysicalPerson);
    }

    /**
     * Función de interfaz encargada de obtener la información de la persona y retornarla
     * en un dto.
     * @param user usuario de la persona.
     * @return Dto. De persona nacional.
     * */
    @Override
    public PhysicalPersonDTO toDto(User user) {
        PhysicalPerson person = getPhysicalPerson(user);
        return personMapper.toDto(user, person);
    }

    /**
     * Función auxiliar encargada de obtener una persona física del repositorio según él,
     * id de persona abstracta.
     * @param user usuario de la persona.
     * @return entidad de la Persona Física recuperada de la bd.
     * @throws ResourceNotFoundException en caso de que la persona física no pueda ser recuperado.
     * */
    private PhysicalPerson getPhysicalPerson(User user){
        return physicalPersonRepository.findById(user.getPerson().getId())
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
