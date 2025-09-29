package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PaymentRegisterDTO;
import com.UNED.APIDataMujer.dto.response.PaymentDTO;
import com.UNED.APIDataMujer.entity.Payment;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.Classification;
import com.UNED.APIDataMujer.enums.Role;
import com.UNED.APIDataMujer.exception.BusinessValidationException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.PaymentMapper;
import com.UNED.APIDataMujer.repository.PaymentRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    @Override
    public PaymentDTO getPayment(long id) {
        var payment = getPaymentById(id);
        return paymentMapper.toDto(payment);
    }

    @Override
    public SimplePage<PaymentDTO> getMyPayments(final Authentication auth, int page) {
        final var user = userService.getMyUser(auth);

        Pageable pageable = PageRequest.of(page, 25,
                Sort.by(Sort.Order.asc("isPaid"), Sort.Order.asc("id")));
        var payments = paymentRepository
                .findByUser(user, pageable);

        return PaginationUtil.wrapInPage(payments, paymentMapper::toDto);
    }

    @Override
    public SimplePage<PaymentDTO> getPaymentsByStatus(boolean isPaid, int page) {
        Pageable pageable = PageRequest.of(page, 24, Sort.by("id"));
        var payments = paymentRepository
                .findByIsPaid(isPaid, pageable);
        return PaginationUtil.wrapInPage(payments, paymentMapper::toDto);
    }

    @Override
    @Transactional
    public PaymentDTO createPayment(final Authentication auth, PaymentRegisterDTO dto) {
        var user = userService.getMyUser(auth);

        if(dto.isPaid() && dto.paymentDate() == null)
            throw new BusinessValidationException("Usted no está indicando que el pago está realizado " +
                    "pero no indica cuando se está realizando.");

        if(dto.paymentDate().isAfter(LocalDateTime.now()))
            throw new BusinessValidationException("La fecha que reporta es inválida.");

        if(dto.classification() == Classification.MENSUALIDAD
        && !user.isAffiliate()) {
            user.setAffiliate(true);
            user = userRepository.save(user);
        }

        var payment = paymentMapper.toEntity(user, dto);
        var myPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(myPayment);
    }

    @Override
    @Transactional
    public PaymentDTO updatePaidRecord(final Authentication auth,
                                       long id, LocalDateTime paymentDate) {

        var payment = getPaymentById(id);

        final var user = userService.getMyUser(auth);

        if(payment.isPaid())
            throw new BusinessValidationException("Este pago cuenta ya con una fecha de transacción y un estado " +
                    "afirmativo.");

        if(user.getRole() != Role.ROLE_ADMIN &&
                !user.equals(payment.getUser()))
            throw new BusinessValidationException("Usted está intentando actualizar un pago " +
                    "que no le corresponde.");

        if(paymentDate.isAfter(LocalDateTime.now()))
            throw new BusinessValidationException("La fecha que reporta es inválida.");

        payment.setPaymentDate(paymentDate);
        payment.setPaid(true);

        var myPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(myPayment);
    }

    @Override
    @Transactional
    public PaymentDTO updateNonPaidRecord(long id) {
        var payment = getPaymentById(id);

        if(!payment.isPaid())
            throw new BusinessValidationException("El pago aún no se reporta como pagado.");

        payment.setPaymentDate(null);
        payment.setPaid(false);

        var myPayment = paymentRepository.save(payment);

        return paymentMapper.toDto(myPayment);
    }

    private Payment getPaymentById(long id){
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El pago "+id+" no se encuentra registrado."));
    }
}
