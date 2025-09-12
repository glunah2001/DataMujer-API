package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.emailing.EmailSendingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ActivationService {

    private final TokenMapper tokenMapper;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final EmailSendingService emailSendingService;

    @Transactional
    public void activateAccount(final String tokenValue) {
        Token token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Su token de activación es inexistente."));

        if(token.isExpired() || token.isRevoked()){
            throw new IllegalArgumentException("Este token de activación ha caducado.");
        }

        User user = token.getUser();
        user.setActive(true);
        userRepository.save(user);

        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    public void generateActivationToken(final User user) {
        String tokenValue = UUID.randomUUID().toString();
        Token token = tokenMapper.toEntity(tokenValue, user, TokenType.ACTIVATION);
        tokenRepository.save(token);

        String activationLink = "https://localhost:8443/activate?token=" + tokenValue;
        emailSendingService.sendEmail(user.getEmail(),
                "Activa tu Cuenta",
                "Haz click en el enlace para activar su cuenta: "+activationLink);
    }
}
