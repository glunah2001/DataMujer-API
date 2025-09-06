package com.UNED.APIDataMujer.service;

import com.UNED.APIDataMujer.dto.*;
import com.UNED.APIDataMujer.dto.authentication.CommonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.entity.*;
import com.UNED.APIDataMujer.enums.PersonType;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.mapper.PersonMapper;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.mapper.UserMapper;
import com.UNED.APIDataMujer.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtService jwtService;

    private final AuthenticationManager authManager;

    private final PersonMapper personMapper;
    private final TokenMapper tokenMapper;
    private final UserMapper userMapper;

    private final LegalPersonRepository legalPersonRepository;
    private final PersonRepository personRepository;
    private final PhysicalPersonRepository physicalPersonRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private record CommonRegisterResult(User user, Person person){};

    @Override
    @Transactional
    public LegalPersonDTO legalRegister(LegalPersonRegisterDTO legalPersonRegisterDTO) {
        CommonRegisterDTO commonDto = legalPersonRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.LEGAL);
        Person person = personData.person;
        User user = personData.user;

        var legalPerson = personMapper.toEntity(person, legalPersonRegisterDTO);
        LegalPerson registeredlegalPerson = legalPersonRepository.save(legalPerson);

        return personMapper.toDto(user, registeredlegalPerson);
    }

    @Override
    @Transactional
    public PhysicalPersonDTO physicalRegister(final PhysicalPersonRegisterDTO physicalRegisterDTO) {
        CommonRegisterDTO commonDto = physicalRegisterDTO.commonRegisterDTO();

        final var personData = commonRegister(commonDto, PersonType.FISICA);
        Person person = personData.person;
        User user = personData.user;

        var physicalPerson = personMapper.toEntity(person, physicalRegisterDTO);
        PhysicalPerson registeredPhysicalPerson = physicalPersonRepository.save(physicalPerson);

        return personMapper.toDto(user, registeredPhysicalPerson);
    }

    @Override
    public TokenResponse login(final UserLoginDTO userLoginDTO) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.username(),
                        userLoginDTO.password()
                )
        );
        var user = userRepository.findByUsername(userLoginDTO.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario no se encuentra registrado en el sistema."));

        return tokenGeneration(user);
    }

    @Override
    public TokenResponse refresh(String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer "))
            throw new IllegalArgumentException("Formato de token inválido.");

        String token = authHeader.substring(7);
        var username = jwtService.getUsername(token);
        if(username == null)
            throw new IllegalArgumentException("El token de refresco es inválido: Nombre de usuario inválido");

        var user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario no se encuentra registrado en el sistema."));

        if(!jwtService.isTokenValid(token, user))
            throw new IllegalArgumentException("El token de refresco es inválido.");

        return tokenGeneration(user);
    }

    private CommonRegisterResult commonRegister(final CommonRegisterDTO commonDto, PersonType personType){
        var person = personMapper.toEntity(commonDto, personType);
        Person registeredPerson = personRepository.save(person);

        var user = userMapper.toEntity(registeredPerson, commonDto);
        User registeredUser = userRepository.save(user);

        return new CommonRegisterResult(registeredUser, registeredPerson);
    }

    private TokenResponse tokenGeneration(final User user){
        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(accessToken, user);
        return new TokenResponse(accessToken, refreshToken);
    }

    private void revokeAllUserToken(final User user){
        final List<Token> tokens = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUserId(user.getId());

        if(tokens.isEmpty()) return;

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(tokens);
    }

    private void saveUserToken(final String jjwt, final User user){
        final var token = tokenMapper.toEntity(jjwt, user);
        tokenRepository.save(token);
    }
}
