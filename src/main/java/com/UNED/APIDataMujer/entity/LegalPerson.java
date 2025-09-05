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
@Table(name = "TBDMDETLegales")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LegalPerson {

    @Id
    @Column(name = "Id")
    private long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "Id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Person person;

    @Column(name = "IdLegal", length = 10, unique = true, nullable = false)
    private String legalId;

    @Column(name = "NombreNegocio", length = 25, nullable = false)
    private String businessName;

    @Column(name = "FechaFundacion", nullable = false)
    private LocalDate foundationDate;
}
