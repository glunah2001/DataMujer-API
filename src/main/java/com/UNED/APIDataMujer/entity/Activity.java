package com.UNED.APIDataMujer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "TBDMMAEActividades")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @Column(name = "Actividad", length = 50, nullable = false)
    private String activity;

    @Column(name = "Descripcion", nullable = false)
    private String description;

    @Column(name = "Ubicacion", nullable = false)
    private String location;

    @Column(name = "EsPresencial", nullable = false)
    private boolean isOnSite;

    @Column(name = "FechaInicio", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime startDate;

    @Column(name = "FechaFin", nullable = false, columnDefinition = "DATETIME")
    private LocalDateTime endDate;

    @Column(name = "EstaCerrada", nullable = false)
    private boolean isFinalized;
}
