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
@Table(name = "TBDMRELVoluntariados")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Volunteering {

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

    @Column(name = "FechaVoluntario", nullable = false)
    private LocalDate volunteerDate;

    @Column(name = "RolEnActividad", length = 15, nullable = false)
    private String activityRole;
}
