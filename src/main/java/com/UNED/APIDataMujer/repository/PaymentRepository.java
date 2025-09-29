package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Payment;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Page<Payment> findByUser(User user, Pageable pageable);
    Page<Payment> findByIsPaid(boolean isPaid, Pageable pageable);
}
