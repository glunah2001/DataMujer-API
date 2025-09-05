package com.UNED.APIDataMujer.entity;

import com.UNED.APIDataMujer.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBDMDETTokens")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Column(name = "Token", nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "TipoToken", nullable = false)
    private TokenType tokenType;

    @Column(name = "EstaRevocado", nullable = false)
    private boolean revoked;

    @Column(name = "EstaExpirado", nullable = false)
    private boolean expired;

    @ManyToOne
    @JoinColumn(name = "UsuariosId", nullable = false)
    private User user;
}
