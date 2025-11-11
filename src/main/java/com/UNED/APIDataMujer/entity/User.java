package com.UNED.APIDataMujer.entity;

import com.UNED.APIDataMujer.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "TBDMMAEUsuarios")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "PersonasId", unique = true, nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;

    @Column(name = "Usuario", length = 15, nullable = false, unique = true)
    private String username;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Contrasena", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "Rol", nullable = false)
    private Role role;

    @Column(name = "FechaRegistro", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "EstaActivo", nullable = false)
    private boolean isActive;

    @Column(name = "EsContribuidor", nullable = false)
    private boolean isContributor;

    @Column(name = "EsAfiliado", nullable = false)
    private boolean isAffiliate;
}
