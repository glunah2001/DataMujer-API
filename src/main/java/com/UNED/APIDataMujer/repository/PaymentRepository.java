package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Payment;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.Classification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUser(User user, Pageable pageable);
    Page<Payment> findByIsPaid(boolean isPaid, Pageable pageable);
    boolean existsByUserAndIsPaidTrue(User user);
    Optional<Payment> findTopByUserAndClassificationAndIsPaidTrueOrderByPaymentDateDesc(
            User user, Classification classification);

    @Query("""
       SELECT p.user
       FROM Payment p
       WHERE p.classification = :classification AND p.isPaid = true
       GROUP BY p.user
       ORDER BY MAX(p.paymentDate) DESC, p.user.isAffiliate ASC
       """)
    Page<User> findUsersWithPaidMonthlyPaymentsPaged(
            @Param("classification") Classification classification,
            Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(p.totalAmount), 0)
            FROM Payment p
            WHERE p.user = :user AND p.isPaid = true AND p.classification = :classification
            """)
    BigDecimal sumPaidMonthlyByUser(@Param("user") User user,
                                    @Param("classification") Classification classification);

}
