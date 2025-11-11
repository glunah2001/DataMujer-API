package com.UNED.APIDataMujer.entity;

import com.UNED.APIDataMujer.enums.Classification;
import com.UNED.APIDataMujer.enums.Method;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TBDMDETPagos")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "UsuariosId", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "Descripcion", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "Clasificacion", nullable = false)
    private Classification classification;

    @Enumerated(EnumType.STRING)
    @Column(name = "Metodo", nullable = false)
    private Method method;

    @Column(name = "FechaPago", columnDefinition = "DATETIME")
    private LocalDateTime paymentDate;

    @Column(name = "EstaPagado", nullable = false)
    private boolean isPaid;

    @Column(name = "MontoTotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
