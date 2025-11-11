package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PaymentRegisterDTO;
import com.UNED.APIDataMujer.dto.response.AffiliatesPaymentReportDTO;
import com.UNED.APIDataMujer.dto.response.PaymentDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

public interface PaymentService {
    PaymentDTO getPayment(long id);
    SimplePage<PaymentDTO> getMyPayments(Authentication auth,
                                         int page);
    SimplePage<PaymentDTO> getPaymentsByStatus(boolean isPaid, int page);
    SimplePage<AffiliatesPaymentReportDTO> generateUserPaymentReport(int page);
    PaymentDTO createPayment(Authentication auth,
                             PaymentRegisterDTO dto);
    PaymentDTO updatePaidRecord(Authentication auth, long id, LocalDateTime paymentDate);
    PaymentDTO updateNonPaidRecord(long id);
    void updateAffiliateAndContributorStatus(User user);
    void deletePayment(long id);
}
