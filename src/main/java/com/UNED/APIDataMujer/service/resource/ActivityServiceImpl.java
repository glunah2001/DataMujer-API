package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.exception.BusinessValidationException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.ActivityMapper;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

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
    private final ActivityRepository activityRepository;

    private final VolunteeringService volunteeringService;

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
            throw new BusinessValidationException("La actividad debe anunciarse como mínimo 1 día " +
                    "de antelación.");

        if(startDate.isAfter(endDate))
            throw new BusinessValidationException("La fecha de inicio de la actividad no puede estar " +
                    "antes que la fecha de cierre.");

        var duration = Duration.between(startDate, endDate);
        if(duration.toMinutes() < 60)
            throw new BusinessValidationException("La duración mínima de la actividad debe ser de " +
                    "1 hora (60 minutos).");

        var newActivity = activityMapper.toEntity(dto);
        final var activity = activityRepository.save(newActivity);

        volunteeringService.createOrganizerVolunteering(dto.username(), activity, startDate, endDate);

        return activityMapper.toDto(activity);
    }

    /**
     * Función encargada de retornar una actividad mediante búsqueda en la bd usando su id
     * @param id id de la actividad
     * @return datos de la actividad recuperada.
     * */
    @Override
    public ActivityDTO getActivityDto(long id) {
        var activity = activityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La actividad con id "+id+" no se ha encontrado."));
        return activityMapper.toDto(activity);
    }

    @Override
    public SimplePage<ActivityDTO> getAllActiveActivities(int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").ascending());
        Page<Activity> activities = activityRepository.findByIsFinalizedFalse(pageable);
        return PaginationUtil.wrapInPage(activities, activityMapper::toDto);
    }
}
