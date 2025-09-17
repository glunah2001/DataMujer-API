package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.register.CommonRegisterDTO;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Mapper encargada de las entidades User. Principalmente usada en el registro.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Función de mapeo manual de una entidad User
     * @param dto contiene toda la información que requieren las personas para registrarse
     *            sin necesidad de distinción de tipo de persona.
     * @param person persona abstracta que representa a dicho usuario
     * @return una entidad User
     * */
    public User toEntity(Person person, CommonRegisterDTO dto){
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.ROLE_STANDARD)
                .registrationDate(LocalDate.now())
                .isActive(false)
                .isContributor(false)
                .isAffiliate(false)
                .person(person)
                .build();
    }

}
