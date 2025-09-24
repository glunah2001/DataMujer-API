package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.ActivityMapper;
import com.UNED.APIDataMujer.mapper.VolunteeringMapper;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import com.UNED.APIDataMujer.repository.VolunteeringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase encargada de gestionar la lógica de negocio relacionada con
 * las actividades. Su creación, actualización y lectura
 * @author glunah2001
 * @see ActivityService
 * */
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final VolunteeringMapper volunteeringMapper;

    private final ActivityRepository activityRepository;
    private final VolunteeringRepository volunteeringRepository;

    private final UserService userService;

    /**
     * Función de interfaz encargada de crear una nueva actividad y
     * delegando la creación de un voluntariado (organizador) al
     * servicio encargado de esto
     * @param dto contiene toda la información de la nueva actividad.
     * @return la actividad registrada en la bd.
     * */
    @Override
    @Transactional
    public ActivityDTO createNewActivity(ActivityRegisterDTO dto) {
        var startDate = dto.startDate();
        var endDate = dto.endDate();

        if(startDate.isBefore(LocalDateTime.now().plusDays(1)))
            throw new IllegalArgumentException("La actividad debe anunciarse con al manos 1 día " +
                    "de antelación");

        if(startDate.isAfter(endDate))
            throw new IllegalArgumentException("La fecha de inicio de la actividad no puede estar " +
                    "antes que la fecha de cierre");

        var duration = Duration.between(startDate, endDate);
        if(duration.toMinutes() < 60)
            throw new IllegalArgumentException("La duración mínima de la actividad debe ser de " +
                    "1 hora (60 minutos)");

        var newActivity = activityMapper.toEntity(dto);
        final var activity = activityRepository.save(newActivity);

        final var user = userService.findUserByUsername(dto.username());

        var conflict = volunteeringRepository.existsOrganizerConflict(
                user.getId(), startDate, endDate);

        if(conflict)
            throw new IllegalArgumentException("El usuario "+user.getUsername()+" ya organiza " +
                    "una actividad cuyo horario entra en conflicto con la que desea crear.");

        var volunteering = volunteeringMapper.toEntity(user, activity);
        volunteeringRepository.save(volunteering);
        return activityMapper.toDto(activity);
    }

    /**
     * Función encargada de retornar una actividad mediante búsqueda en la bd usando su id
     * @param id id de la actividad
     * @return datos de la actividad recuperada.
     * */
    @Override
    public ActivityDTO getActivityDto(long id) {
        var activity = getActivity(id);
        return activityMapper.toDto(activity);
    }

    @Override
    public Activity getActivity(long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("La actividad con id "+id+" no se ha encontrado."));
    }

    @Override
    public List<ActivityDTO> getAllActiveActivities() {
        var activities = activityRepository.findByIsFinalizedFalse();
        return activities.stream()
                .map(activityMapper::toDto)
                .toList();
    }
}
