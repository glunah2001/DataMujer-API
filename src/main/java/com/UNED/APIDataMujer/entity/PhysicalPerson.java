package com.UNED.APIDataMujer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity
@Table(name = "TBDMDETFisicas")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PhysicalPerson {

    @Id
    @Column(name = "Id")
    private long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "Id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;

    @Column(name = "IdNacional", length = 12, nullable = false, unique = true)
    private String nationalId;

    @Column(name = "PrimerApellido", length = 15, nullable = false)
    private String firstSurname;

    @Column(name = "SegundoApellido", length = 15, nullable = false)
    private String secondSurname;

    @Column(name = "Nombre", length = 15, nullable = false)
    private String name;

    @Column(name = "Profesion", length = 25, nullable = false)
    private String profession;

    @Column(name = "FechaNacimiento", nullable = false)
    private LocalDate birthDate;
}
