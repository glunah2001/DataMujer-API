package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Participation;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.Classification;
import com.UNED.APIDataMujer.enums.ParticipationState;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import com.UNED.APIDataMujer.repository.ParticipationRepository;
import com.UNED.APIDataMujer.repository.PaymentRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Este bean de servicio tiene el único proposition de ejecutar tareas concretas
 * automáticamente.
 * @author glunah2001
 * */
@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final ActivityRepository activityRepository;
    private final ParticipationRepository participationRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    /**
     * TODOS LOS DÍAS A LAS 23:50 (11:50 PM) revisará las actividades
     * cuya fecha de cierre haya pasado e irá clausurando sus participaciones como
     * COMPLETAS en caso de que la persona haya indicado el inicio de su participación o
     * CANCELADA en caso contrario para finalmente actualizar los cambios en la BD.
     * */
    @Scheduled(cron = "0 50 23 * * ?")
    @Transactional
    public void scheduledActivityCheck(){
        List<Activity> activities = activityRepository
                .findByIsFinalizedFalseAndEndDateBefore(LocalDateTime.now());
        activities.forEach(activity -> {
            activity.setFinalized(true);

            List<Participation> participation = participationRepository
                    .findByActivityIdAndStatusIn(activity.getId(),
                            List.of(ParticipationState.PENDIENTE,
                                    ParticipationState.AVANZANDO
                    ));

            participation.forEach(singleParticipation -> {
                if(singleParticipation.getStatus() == ParticipationState.PENDIENTE){
                    singleParticipation.setStatus(ParticipationState.CANCELADO);
                }else{
                    singleParticipation.setStatus(ParticipationState.COMPLETADO);
                    singleParticipation.setEndDate(LocalDate.now());
                }
            });
            participationRepository.saveAll(participation);
        });
        activityRepository.saveAll(activities);
    }

    /**
     * Función que revisa al inicio del día si la persona tiene un pago MENSUALIDAD del mes actual
     * para actualizar su estado.
     * */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void verifyAffiliateStatus() {
        String currentMonthYear = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        List<User> users = userRepository.findAll();
        users.forEach(user -> {
            boolean hasMonthlyPayment = paymentRepository
                    .existsByUserAndClassificationAndIsPaidAndPaymentMonthYear(
                            user,
                            Classification.MENSUALIDAD,
                            true,
                            currentMonthYear
                    );

            if (hasMonthlyPayment && !user.isContributor()) {
                user.setContributor(true);
                userRepository.save(user);
            } else if (!hasMonthlyPayment && user.isContributor()) {
                user.setContributor(false);
                userRepository.save(user);
            }
        });
    }

}
