package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.request.PaymentRegisterDTO;
import com.UNED.APIDataMujer.dto.response.AffiliatesPaymentReportDTO;
import com.UNED.APIDataMujer.dto.response.PaymentDTO;
import com.UNED.APIDataMujer.entity.Payment;
import com.UNED.APIDataMujer.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;


/**
 * Mapper de entidad Payment de entidad a DTO y viceversa.
 * @author AHKolodin
 * */
@Component
@RequiredArgsConstructor
public class PaymentMapper {

    /**
     * Mapeo manual de Payment de DTO a entidad.
     * @param dto contiene los datos del pago
     * @param user usuario al que le pertenece el pago.
     * @return entidad payment.
     * */
    public Payment toEntity(User user, PaymentRegisterDTO dto){
        return Payment.builder()
                .user(user)
                .description(dto.description())
                .classification(dto.classification())
                .method(dto.method())
                .paymentDate(dto.paymentDate())
                .isPaid(dto.isPaid())
                .totalAmount(dto.totalAmount())
                .build();
    }

    /**
     * Mapeo a dto. De una entidad payment.
     * @param payment entidad recuperada de la base de datos.
     * @return dto. Del pago.
     * */
    public PaymentDTO toDto(Payment payment){
        return new PaymentDTO(
                payment.getId(),
                payment.getUser().getUsername(),
                payment.getDescription(),
                payment.getClassification(),
                payment.getMethod(),
                payment.getPaymentDate(),
                payment.isPaid(),
                payment.getTotalAmount()
                );
    }

    /**
     * Mapeo a dto. De detalles de pago de varios usuarios afiliados.
     * @param user usuario en cuestión.
     * @param lastPaymentDate última fecha de pago.
     * @param totalPaid total pagado en mensualidades.
     * @return dto con la información de pago de un usuario.
     * */
    public AffiliatesPaymentReportDTO toDto(User user, LocalDateTime lastPaymentDate,
                                            BigDecimal totalPaid){
        return new AffiliatesPaymentReportDTO(
                user.getUsername(),
                lastPaymentDate,
                lastPaymentDate.plusMonths(1),
                totalPaid,
                user.isAffiliate()
        );
    }

}
