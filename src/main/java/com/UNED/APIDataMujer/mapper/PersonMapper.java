package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.*;
import com.UNED.APIDataMujer.dto.authentication.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.entity.LegalPerson;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.PersonType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    public Person toEntity(CommonRegisterDTO dto, PersonType personType){
        return Person.builder()
                .personType(personType)
                .phoneNumber(dto.phoneNumber())
                .country(dto.country())
                .location(dto.location())
                .build();
    }

    public PhysicalPerson toEntity(Person person, PhysicalPersonRegisterDTO dto){
        return PhysicalPerson.builder()
                .person(person)
                .nationalId(dto.nationalId())
                .name(dto.name())
                .firstSurname(dto.firstSurname())
                .secondSurname(dto.secondSurname())
                .profession(dto.profession())
                .birthDate(dto.birthDate())
                .build();
    }

    public LegalPerson toEntity(Person person, LegalPersonRegisterDTO dto){
        return LegalPerson.builder()
                .person(person)
                .legalId(dto.legalId())
                .businessName(dto.businessName())
                .foundationDate(dto.foundationDate())
                .build();
    }

    public PhysicalPersonDTO toDto(User user, PhysicalPerson physicalPerson){
        Person person = physicalPerson.getPerson();
        return new PhysicalPersonDTO(
                physicalPerson.getNationalId(),
                physicalPerson.getFirstSurname(),
                physicalPerson.getSecondSurname(),
                physicalPerson.getName(),
                physicalPerson.getProfession(),
                physicalPerson.getBirthDate(),
                person.getPhoneNumber(),
                person.getCountry(),
                person.getLocation(),
                user.getUsername(),
                user.getEmail());
    }

    public LegalPersonDTO toDto(User user, LegalPerson legalPerson){
        Person person = legalPerson.getPerson();
        return new LegalPersonDTO(
                legalPerson.getLegalId(),
                legalPerson.getBusinessName(),
                legalPerson.getFoundationDate(),
                person.getPhoneNumber(),
                person.getCountry(),
                person.getLocation(),
                user.getUsername(),
                user.getEmail());
    }

}
