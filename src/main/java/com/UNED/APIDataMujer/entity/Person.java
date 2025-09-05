package com.UNED.APIDataMujer.entity;

import com.UNED.APIDataMujer.enums.Country;
import com.UNED.APIDataMujer.enums.PersonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TBDMMAEPersonas")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "TipoPersona", nullable = false)
    private PersonType personType;

    @Column(name = "Telefono", length = 17, unique = true, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "Pais", length = 35, nullable = false)
    private Country country;

    @Column(name = "Ubicacion", nullable = false)
    private String location;
}
