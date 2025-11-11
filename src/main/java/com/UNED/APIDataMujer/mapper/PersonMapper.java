package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.request.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.response.LegalPersonDTO;
import com.UNED.APIDataMujer.dto.response.PhysicalPersonDTO;
import com.UNED.APIDataMujer.entity.LegalPerson;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.PhysicalPerson;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.PersonType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mapper de entidad Person de entidad a DTO y viceversa.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class PersonMapper {

    /**
     * Mapeo manual de Person de DTO a entidad.
     * @param dto requiere los datos comunes de registro o actualización.
     * @param personType dado que Person hace referencia a persona abstracta requiere
     *                   saber desde aquí qué tipo de persona es el usuario.
     * @return entidad Person
     * */
    public Person toEntity(CommonRegisterDTO dto, PersonType personType){
        return Person.builder()
                .personType(personType)
                .phoneNumber(dto.phoneNumber())
                .country(dto.country())
                .location(dto.location())
                .build();
    }

    /**
     * Mapeo manual de Persona Física de DTO a entidad.
     * @param person entidad persona abstracta.
     * @param dto información específica de personas físicas.
     * @return entidad PhysicalPerson.
     * */
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

    /**
     * Mapeo manual de Persona jurídica de DTO a entidad.
     * @param person entidad persona abstracta.
     * @param dto información específica de personas jurídica.
     * @return entidad LegalPerson.
     * */
    public LegalPerson toEntity(Person person, LegalPersonRegisterDTO dto){
        return LegalPerson.builder()
                .person(person)
                .legalId(dto.legalId())
                .businessName(dto.businessName())
                .foundationDate(dto.foundationDate())
                .build();
    }

    /**
     * Mapeo manual de Persona Física de entidad a DTO.
     * @param user entidad user de la cual sacar información común.
     * @param physicalPerson entidad de persona física.
     * @return Dto. Con toda la información pública no comprometida de una persona Física.
     * */
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

    /**
     * Mapeo manual de Persona jurídica de entidad a DTO.
     * @param user entidad user de la cual sacar información común.
     * @param legalPerson entidad de persona jurídica.
     * @return Dto. Con toda la información pública no comprometida de una persona jurídica.
     * */
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
