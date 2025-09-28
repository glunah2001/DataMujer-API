package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.response.ParticipationDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Participation;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.ParticipationState;
import com.UNED.APIDataMujer.enums.Role;
import com.UNED.APIDataMujer.exception.BusinessValidationException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.ParticipationMapper;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import com.UNED.APIDataMujer.repository.ParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParticipationServiceImpl implements ParticipationService{

    private final UserService userService;
    private final ParticipationMapper participationMapper;
    private final ActivityRepository activityRepository;
    private final ParticipationRepository participationRepository;


    /**
     * Función que recupera la información de una participación recuperada por ID.
     * @param participationId identificador de la participación.
     * @return Dto. Con la información del voluntariado.
     * */
    @Override
    public ParticipationDTO getParticipation(long participationId) {
        var participation = getParticipationById(participationId);
        return participationMapper.toDto(participation);
    }

    /**
     * Función de interfaz encargada de obtener todas las participaciones no canceladas de
     * actividades no finalizadas.
     * @param auth credenciales.
     * @param page paginación.
     * @return paginación de las participaciones.
     * */
    @Override
    public SimplePage<ParticipationDTO> getMyParticipations(final Authentication auth,
                                                            int page) {
        final var user = userService.getMyUser(auth);

        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").ascending());
        Page<Participation> participation = participationRepository
                .findByUserIdAndActivityIsFinalizedFalseAndStatusNot(user.getId(),
                        ParticipationState.CANCELADO,
                        pageable);

        return PaginationUtil.wrapInPage(participation, participationMapper::toDto);
    }

    /**
     * Función de interfaz encargada de crear una nueva participación para una actividad.
     * @param auth credenciales
     * @param activityId identificador de la actividad
     * @return datos de la participación
     * @throws ResourceNotFoundException en caso de que la actividad no sea encontrada.
     * @throws BusinessValidationException en caso de que la actividad esté finalizada o
     *  se intente registrar después de la fecha de inicio.
     * */
    @Override
    @Transactional
    public ParticipationDTO createParticipation(final Authentication auth, long activityId) {
        final var user = userService.getMyUser(auth);
        final var activity = activityRepository.findById(activityId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La actividad "+activityId+" no fue encontrada para " +
                                "colocar su participación."));
        if(activity.isFinalized())
            throw new BusinessValidationException("La actividad "+activityId+" está actualmente clausurada.");

        if(activity.getStartDate().isBefore(LocalDateTime.now()))
            throw new BusinessValidationException("La actividad "+activityId+" ya ha dado inicio, por lo que su " +
                    "registro para participación no se puede lleva a cabo.");

        var participation = participationMapper.toEntity(user, activity);
        var myParticipation = participationRepository.save(participation);

        return participationMapper.toDto(myParticipation);
    }

    /**
     * Función de interfaz encargada de indicar que una participación ha dado inicio.
     * @param auth credenciales
     * @param participationId identificador de la participación
     * @return datos de la participación
     *  se intenta actualizar una actividad que no corresponde al usuario.
     * */
    @Override
    @Transactional
    public ParticipationDTO updateStartDate(Authentication auth, long participationId) {
        var participation = getParticipationById(participationId);

        final var user = userService.getMyUser(auth);

        validateParticipation(participation, user);

        participation.setStartDate(LocalDate.now());
        participation.setStatus(ParticipationState.AVANZANDO);

        var myParticipation = participationRepository.save(participation);
        return participationMapper.toDto(myParticipation);
    }

    /**
     * Función de interfaz encargada de indicar que una participación se ha cancelado.
     * @param auth credenciales
     * @param participationId identificador de la participación
     * @return datos de la participación
     * */
    @Override
    @Transactional
    public ParticipationDTO cancelParticipation(final Authentication auth, long participationId) {
        var participation = getParticipationById(participationId);

        final var user = userService.getMyUser(auth);

        validateParticipation(participation, user);

        participation.setEndDate(LocalDate.now());
        participation.setStatus(ParticipationState.CANCELADO);
        var myParticipation = participationRepository.save(participation);
        return participationMapper.toDto(myParticipation);
    }

    /**
     * Función de interfaz encargada de eliminar una participación.
     * @param auth credenciales
     * @param participationId identificador de la participación
     * */
    @Override
    @Transactional
    public void deleteParticipation(final Authentication auth, long participationId) {
        var participation = getParticipationById(participationId);
        final var user = userService.getMyUser(auth);

        if(user.getRole() != Role.ROLE_ADMIN)
            validateParticipation(participation, user);

        participationRepository.delete(participation);
    }

    /**
     * Función encargada de recuperar una participación por ID de la base de datos.
     * @param participationId identificador de la participación.
     * @throws ResourceNotFoundException en caso de que la participación no sea encontrada.
     * */
    private Participation getParticipationById(long participationId){
        return participationRepository.findById(participationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La participación "+participationId+" no fue encontrada " +
                                "para llevar a cabo la actualización"));
    }

    /**
     * Función encargada de validar algunos puntos en la participación.
     * @param user usuario de la participación.
     * @param participation participación recuperada.
     * @throws BusinessValidationException en caso de que la actividad esté finalizada o no
     * le pertenezca al usuario.
     */
    private void validateParticipation(Participation participation, User user){
        var participationId = participation.getId();
        if(!user.equals(participation.getUser()))
            throw new BusinessValidationException("La participación "+participationId+" no corresponde a su usuario");

        if(participation.getActivity().isFinalized())
            throw new BusinessValidationException("La participación "+participationId+" pertenece a una actividad " +
                    "ya clausurada. La operación no puede darse a cabo.");
    }

}
