package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.PaymentRegisterDTO;
import com.UNED.APIDataMujer.dto.response.AffiliatesPaymentReportDTO;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase encargada a los servicios relacionados con los pagos.
 * @author AHKolodin
 * @see PaymentService
 * */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService{

    private final UserService userService;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;

    /**
     * Función encargada de obtener un pago por ID.
     * @param id identificador.
     * @return pago recuperado en DTO.
     * */
    @Override
    public PaymentDTO getPayment(long id) {
        var payment = getPaymentById(id);
        return paymentMapper.toDto(payment);
    }

    /**
     * Función encargada de obtener una serie de pagos asociados
     * al usuario que solicita.
     * @param auth credenciales.
     * @param page pagina.
     * @return listado de los pagos ordenados por estado y luego id.
     * */
    @Override
    public SimplePage<PaymentDTO> getMyPayments(final Authentication auth,
                                                int page) {
        final var user = userService.getMyUser(auth);

        Pageable pageable = PageRequest.of(page, 25,
                Sort.by(Sort.Order.asc("isPaid"), Sort.Order.asc("id")));
        var payments = paymentRepository
                .findByUser(user, pageable);

        return PaginationUtil.wrapInPage(payments, paymentMapper::toDto);
    }

    /**
     * Obtener una serie de pagos dependiendo si están o no pagados.
     * @param isPaid estado con el cual filtrar.
     * @param page página.
     * @return págína con los resultados en DTO.
     * */
    @Override
    public SimplePage<PaymentDTO> getPaymentsByStatus(boolean isPaid, int page) {
        Pageable pageable = PageRequest.of(page, 24, Sort.by("id"));
        var payments = paymentRepository
                .findByIsPaid(isPaid, pageable);
        return PaginationUtil.wrapInPage(payments, paymentMapper::toDto);
    }

    /**
     * Función encargada de consultar la información de todos los usuarios que han
     * pagado al menos una mensualidad.
     * @param page número de página.
     * @return una página con los resultados.
     * */
    @Override
    public SimplePage<AffiliatesPaymentReportDTO> generateUserPaymentReport(int page) {

        Pageable pageable = PageRequest.of(page, 25); // 25 por página
        var users = paymentRepository
                .findUsersWithPaidMonthlyPaymentsPaged(Classification.MENSUALIDAD, pageable);

        return PaginationUtil.wrapInPage(users, user -> {
            var lastPaymentOpt = paymentRepository
                    .findTopByUserAndClassificationAndIsPaidTrueOrderByPaymentDateDesc(
                            user, Classification.MENSUALIDAD);

            LocalDateTime lastPaymentDate = lastPaymentOpt.map(Payment::getPaymentDate).orElse(null);

            BigDecimal totalPaid = paymentRepository
                    .sumPaidMonthlyByUser(user, Classification.MENSUALIDAD);

            return paymentMapper.toDto(user, lastPaymentDate, totalPaid);
        });
    }

    /**
     * Función para crear un nuevo pago.
     * @param auth credenciales.
     * @param dto información del pago.
     * @throws BusinessValidationException en caso de que una regla de negocio sea violada como:
     * fecha nula cuando se reporta que el pago se realizó o fecha enviada cuando se reporta
     * que el pago está pendiente.
     * @return DTO. Con información del nuevo pago registrado.
     * */
    @Override
    @Transactional
    public PaymentDTO createPayment(final Authentication auth, PaymentRegisterDTO dto) {
        var user = userService.getMyUser(auth);

        if(dto.isPaid() && dto.paymentDate() == null)
            throw new BusinessValidationException("Usted no está indicando que el pago está realizado " +
                    "pero no indica cuando se está realizando.");
        if(!dto.isPaid() && dto.paymentDate() != null)
            throw new BusinessValidationException("Dado que está reportando un pago pendiente, no indique una " +
                    "fecha de pago.");

        var payment = paymentMapper.toEntity(user, dto);
        var myPayment = paymentRepository.save(payment);

        updateAffiliateAndContributorStatus(user);

        return paymentMapper.toDto(myPayment);
    }

    /**
     * Función para actualizar a estado PAGADO a un pago realizado.
     * @param auth credenciales.
     * @param id identificador del pago.
     * @param paymentDate nueva fecha de pago.
     * @throws BusinessValidationException en caso de que una regla de negocio sea violada como:
     * fecha nula, el pago ya está marcado como realizado.
     * @return DTO. Con información del nuevo pago registrado.
     * */
    @Override
    @Transactional
    public PaymentDTO updatePaidRecord(final Authentication auth,
                                       long id, LocalDateTime paymentDate) {

        var payment = getPaymentById(id);
        final var user = userService.getMyUser(auth);

        if(user.getRole() != Role.ROLE_ADMIN &&
                !user.equals(payment.getUser()))
            throw new BusinessValidationException("Usted está intentando actualizar un pago " +
                    "que no le corresponde.");

        if(payment.isPaid())
            throw new BusinessValidationException("Este pago cuenta ya con una fecha de transacción y un estado " +
                    "afirmativo.");

        if(paymentDate.isAfter(LocalDateTime.now()))
            throw new BusinessValidationException("La fecha que reporta es inválida.");

        payment.setPaymentDate(paymentDate);
        payment.setPaid(true);

        var myPayment = paymentRepository.save(payment);
        updateAffiliateAndContributorStatus(user);

        return paymentMapper.toDto(myPayment);
    }

    /**
     * Función para actualizar a estado PAGADO a un pago realizado.
     * @param id identificador del pago.
     * @throws BusinessValidationException en caso de que una regla de negocio sea violada. El pago
     * ya se reportó como pendiente.
     * @return DTO. Con información del pago.
     * */
    @Override
    @Transactional
    public PaymentDTO updateNonPaidRecord(long id) {
        var payment = getPaymentById(id);

        if(!payment.isPaid())
            throw new BusinessValidationException("El pago aún no se reporta como pagado.");

        payment.setPaymentDate(null);
        payment.setPaid(false);

        var myPayment = paymentRepository.save(payment);
        updateAffiliateAndContributorStatus(myPayment.getUser());

        return paymentMapper.toDto(myPayment);
    }

    /**
     * Función que actualiza los estados "Contribuidor" y "Afiliado" a true de
     * un usuario.
     * @param user usuario a actualizar.
     * */
    @Override
    @Transactional
    public void updateAffiliateAndContributorStatus(User user){
        var hasContributions = paymentRepository.existsByUserAndIsPaidTrue(user);
        user.setContributor(hasContributions);

        var lastMonthlyPayment = paymentRepository
                .findTopByUserAndClassificationAndIsPaidTrueOrderByPaymentDateDesc(
                        user, Classification.MENSUALIDAD
                );

        if (lastMonthlyPayment.isPresent()) {
            var paymentDate = lastMonthlyPayment.get().getPaymentDate();
            var expirationDate = paymentDate.plusMonths(1);

            user.setAffiliate(
                    expirationDate.isAfter(LocalDateTime.now())
            );
        } else {
            user.setAffiliate(false);
        }
        userRepository.save(user);
    }



    /**
     * Función encargada de eliminar un payment de la base de datos.
     * @param id identificador del pago.
     * */
    @Override
    @Transactional
    public void deletePayment(long id) {
        var payment = getPaymentById(id);
        paymentRepository.delete(payment);
    }

    /**
     * Función que recupera la entidad de la base de datos.
     * @param id identificador del pago.
     * @return entidad Payment.
     * @throws ResourceNotFoundException en caso de que la entidad no sea encontrada.
     * */
    private Payment getPaymentById(long id){
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El pago "+id+" no se encuentra registrado."));
    }
}
