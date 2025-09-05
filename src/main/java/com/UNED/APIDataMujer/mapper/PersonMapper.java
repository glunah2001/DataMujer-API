package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonDTO;
import com.UNED.APIDataMujer.dto.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.PersonType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonMapper {

    public Person ToEntity(CommonRegisterDTO dto){
        return Person.builder()
                .personType(PersonType.FISICA)
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

    public PhysicalPersonDTO toDto(Person person, User user, PhysicalPerson physicalPerson){
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

}
