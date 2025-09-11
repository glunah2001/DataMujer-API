package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.register.CommonRegisterDTO;
import com.UNED.APIDataMujer.entity.Person;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public User toEntity(Person person, CommonRegisterDTO dto){
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.ROLE_GUEST)
                .registrationDate(LocalDate.now())
                .isActive(false)
                .isContributor(false)
                .isAffiliate(false)
                .person(person)
                .build();
    }

}
