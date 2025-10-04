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

@Component
@RequiredArgsConstructor
public class PaymentMapper {

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
