package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.request.PaymentRegisterDTO;
import com.UNED.APIDataMujer.service.resource.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;

/**
 * Controlador encargado de atender peticiones relacionadas con los pagos.
 * @author AHKolodin
 * @see PaymentService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Endpoint para consultar un solo pago por ID
     * @param id identificador del pago
     * @return dto. Con información del pago.
     * */
    @GetMapping
    public ResponseEntity<?> getPayment(@RequestParam(defaultValue = "0") long id){
        var payment = paymentService.getPayment(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Endpoint para obtener todos los pagos según su estado.
     * @param isPaid estado. False = pendientes. True = pagados.
     * @param page página de búsqueda.
     * @return resultado de búsqueda con dto. Con información del pago.
     * */
    @GetMapping("/status")
    public ResponseEntity<?> getPaymentsByStatus(@RequestParam(defaultValue = "true") boolean isPaid,
                                                       @RequestParam(defaultValue = "0") int page){
        var payments = paymentService.getPaymentsByStatus(isPaid, page);
        return ResponseEntity.ok(payments);
    }

    /**
     * Endpoint para obtener los pagos únicos de la persona.
     * @param auth credenciales.
     * @param page página de búsqueda.
     * @return resultado de búsqueda con dto. Con información del pago.
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getMyPayments(final Authentication auth,
                                           @RequestParam(defaultValue = "0") int page){
        var payments = paymentService.getMyPayments(auth, page);
        return ResponseEntity.ok(payments);
    }

    /**
     * Endpoint para reportar un nuevo pago.
     * @param auth credenciales.
     * @param dto Dto. Con información del pago realizado.
     * @return código 201.
     * */
    @PostMapping
    public ResponseEntity<?> createPayment(final Authentication auth,
                                           @Valid @RequestBody final PaymentRegisterDTO dto){
        var payment = paymentService.createPayment(auth, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/payment")
                .queryParam("id", payment.id())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(payment);
    }

    /**
     * Endpoint para indicar que un pago reportado como pendiente ahora se ha pagado.
     * @param auth credenciales.
     * @param id identificador del pago.
     * @param date la fecha en la que el pago fue hecho.
     * @return código 200 con DTO. Con los datos actualizados.
     * */
    @PutMapping("/paid")
    public ResponseEntity<?> updatePaidPayment(final Authentication auth,
                                               @RequestParam(defaultValue = "0") long id,
                                               @RequestParam LocalDateTime date){
        var payment = paymentService.updatePaidRecord(auth, id, date);
        return ResponseEntity.ok(payment);
    }

    /**
     * Endpoint reservado a administradores únicamente para corregir un pago reportado
     * como realizado y revertirlo a pendiente.
     * @param id identificador del pago.
     * @return código 200 con DTO. Con los datos actualizados.
     * */
    @PutMapping("/unpaid")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> updateUnpaidPayment(@RequestParam(defaultValue = "0") long id){
        var payment = paymentService.updateNonPaidRecord(id);
        return ResponseEntity.ok(payment);
    }

    /**
     * Endpoint restringido a administradores para eliminar un pago.
     * @param id identificador del pago.
     * */
    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deletePayment(@RequestParam(defaultValue = "0") long id){
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
