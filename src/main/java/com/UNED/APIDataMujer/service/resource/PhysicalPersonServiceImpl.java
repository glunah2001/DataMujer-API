package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.repository.PhysicalPersonRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhysicalPersonServiceImpl implements PhysicalPersonService{

    private final PhysicalPersonRepository physicalPersonRepository;
    private final UserRepository userRepository;
    private final PersonMapper personMapper;

    @Override
    public PhysicalPersonDTO getPersonByNationalId(String nationalId) {
        var pp = physicalPersonRepository.findByNationalId(nationalId)
                .orElseThrow(() -> new IllegalArgumentException("La persona física con " +
                        "identificación "+nationalId+" no fue encontrada."));
        var user = getUserByPerson(pp.getPerson());
        return personMapper.toDto(user, pp);
    }

    @Override
    public SimplePage<PhysicalPersonDTO> getPersonByName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var nameSearch = physicalPersonRepository.findByNameContainingIgnoreCase(name, pageable);
        return PaginationUtil.wrapInPage(nameSearch,
                pp -> personMapper.toDto(
                        getUserByPerson(pp.getPerson()), pp));
    }

    @Override
    public SimplePage<PhysicalPersonDTO> getPersonBySurname(String surname, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var surnameSearch = physicalPersonRepository
                .findByAnySurnameContainingIgnoreCase(surname, pageable);
        return PaginationUtil.wrapInPage(surnameSearch,
                pp -> personMapper.toDto(
                        getUserByPerson(pp.getPerson()), pp));
    }

    @Override
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

    @Override
    public PhysicalPersonDTO toDto(User user) {
        PhysicalPerson person = getPhysicalPerson(user);
        return personMapper.toDto(user, person);
    }

    private PhysicalPerson getPhysicalPerson(User user){
        return physicalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() -> new IllegalArgumentException("Ocurrió un error al " +
                        "intentar obtener su información"));
    }

    private User getUserByPerson(Person person){
        return userRepository.findByPerson(person)
                .orElseThrow(() ->
                        new IllegalArgumentException("El usuario no se ha encontrado."));
    }
}
