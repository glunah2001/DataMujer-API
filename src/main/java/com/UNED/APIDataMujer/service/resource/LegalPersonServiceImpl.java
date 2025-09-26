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

@Service
@RequiredArgsConstructor
public class LegalPersonServiceImpl implements LegalPersonService{

    private final UserRepository userRepository;
    private final LegalPersonRepository legalPersonRepository;
    private final PersonMapper personMapper;

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

    @Override
    public SimplePage<LegalPersonDTO> getPersonByBusinessName(String name, int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id"));
        var businessNameSearch = legalPersonRepository.findByBusinessNameContainingIgnoreCase(name, pageable);

        return PaginationUtil.wrapInPage(businessNameSearch,
                lp -> personMapper.toDto(
                        getUserByPerson(lp.getPerson()), lp));
    }

    @Override
    @Transactional
    public LegalPersonDTO updateMyLegalProfile(User user, LegalPersonUpdateDTO dto) {
        var legalPerson = getLegalPerson(user);

        legalPerson.setBusinessName(dto.businessName());
        legalPerson.setFoundationDate(dto.foundationDate());
        var updatedLegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, updatedLegalPerson);
    }

    @Override
    public LegalPersonDTO toDto(User user) {
        LegalPerson person = getLegalPerson(user);
        return personMapper.toDto(user, person);
    }


    private LegalPerson getLegalPerson(User user){
        return legalPersonRepository.findById(user.getPerson().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No es posible recuperar información " +
                                "de su persona.")
                );
    }

    private User getUserByPerson(Person person){
        return userRepository.findByPerson(person)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No es posible recuperar información " +
                                "de su persona.")
                );
    }
}
