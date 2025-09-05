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
@Table(name = "TBDMRELAcciones")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Participation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "ActividadesId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "UsuariosId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "FechaRegistro", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "FechaInicio", nullable = false)
    private LocalDate startDate;

    @Column(name = "FechaFin")
    private LocalDate endDate;

    @Column(name = "HorasSemanales", nullable = false)
    private float weeklyHours;
}
