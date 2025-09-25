package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.BaseVolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Volunteering;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.VolunteeringMapper;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import com.UNED.APIDataMujer.repository.VolunteeringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Clase encargada de gestionar la lógica de negocio relacionada con los
 * voluntariados de las personas en actividades. Cuando se refiere a
 * Voluntariados se refieren a puestos como: Organizador, Mentor encargado,
 * Moderador, etc.
 * @author glunah2001
 * @see VolunteeringService
 * */
@Service
@RequiredArgsConstructor
public class VolunteeringServiceImpl implements VolunteeringService{

    private final UserService userService;
    private final VolunteeringMapper volunteeringMapper;

    private final VolunteeringRepository volunteeringRepository;
    private final ActivityRepository activityRepository;

    @Override
    public VolunteeringDTO getVolunteering(long id) {
        var volunteering = volunteeringRepository.findById(id)
                .orElseThrow(() -> new
                        ResourceNotFoundException("El voluntariado referenciado con " +
                        "id "+ id +" no se ha encontrado"));
        return volunteeringMapper.toDto(volunteering);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * pendientes (de actividades sin finalizar) de la persona que solicita.
     * @param auth credenciales de autentificación del usuario
     * @return lista de voluntariados con detalles de la actividad
     * */
    @Override
    public SimplePage<VolunteeringDTO> getMyPendingVolunteering(Authentication auth,
                                                                @RequestParam(defaultValue = "0") int page) {
        final var user = userService.getMyUser(auth);
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").ascending());
        Page<Volunteering> volunteering = volunteeringRepository
                                            .findByUserIdAndActivityIsFinalizedFalse(user.getId(), pageable);
        return PaginationUtil.wrapInPage(volunteering, volunteeringMapper::toDto);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * de una actividad.
     * @param id id de la actividad a buscar
     * @return lista de voluntariados con detalles de la actividad.
     * */
    @Override
    public SimplePage<VolunteeringDTO> getVolunteeringForAnActivity(long id,
                                                                    @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").ascending());
        Page<Volunteering> volunteering = volunteeringRepository
                                            .findByActivityId(id, pageable);
        return PaginationUtil.wrapInPage(volunteering, volunteeringMapper::toDto);
    }

    @Override
    public void createOrganizerVolunteering(String username,
                                            Activity activity,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate) {

        final var user = userService.getUserByUsername(username);

        var conflict = volunteeringRepository.existsOrganizerConflict(
                user.getId(), startDate, endDate);
        if(conflict)
            throw new IllegalArgumentException("El usuario "+username+" ya organiza " +
                    "una actividad cuyo horario entra en conflicto con la que desea crear.");

        var volunteering = volunteeringMapper.toEntity(user, activity);

        volunteeringRepository.save(volunteering);
    }

    @Override
    @Transactional
    public long createVolunteering(VolunteeringWrapperDTO dto) {
        var list = dto.volunteering();
        if(list.isEmpty())
            throw new IllegalArgumentException("Información de voluntariados vacía");

        var activityId = dto.activityId();
        if(!sameActivityVolunteering(activityId, list))
            throw new IllegalArgumentException(("Inconsistencia de datos detectada: Voluntariados para " +
                    "diferentes actividades."));

        list.forEach(this::createVolunteering);
        return activityId;
    }

    @Override
    @Transactional
    public VolunteeringDTO createMyVolunteering(final Authentication auth,
                                                BaseVolunteeringRegisterDTO dto) {
        var user = userService.getMyUser(auth);
        var volunteering = new VolunteeringRegisterDTO(dto, user.getUsername());
        return createVolunteering(volunteering);
    }


    @Transactional
    public VolunteeringDTO createVolunteering(VolunteeringRegisterDTO dto) {
        var volunteeringData = dto.volunteeringData();

        final var activity = getActivity(volunteeringData.activityId());
        var username = dto.username();

        if(activity.isFinalized())
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" se está registrando en una actividad clausurada.");

        if(!dto.volunteeringData().startShift().isBefore(dto.volunteeringData().endShift()))
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" indica que la fecha de inicio está después de la fecha de " +
                    "cierre de su turno.");

        if(dto.volunteeringData().startShift().isBefore(activity.getStartDate()) ||
                dto.volunteeringData().endShift().isAfter(activity.getEndDate()))
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" se está registrando fuera del rango de la actividad.");

        var shiftLength = ChronoUnit.HOURS.between(dto.volunteeringData().startShift(), dto.volunteeringData().endShift());
        if(shiftLength < 1 || shiftLength> 8)
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" debe abarcar una cantidad de horas razonables. " +
                    "De 1 a 8 horas por turno.");

        final var user = userService.getUserByUsername(username);
        var organizerId = volunteeringRepository.findOrganizerIdByActivityId(activity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ocurrió un error al consultar organizador " +
                        "de la actividad"));

        var sameOrganizerConflict = volunteeringRepository.existsConflictWithSameOrganizer(
                user.getId(), organizerId, activity.getId(), dto.volunteeringData().startShift(), dto.volunteeringData().endShift());

        if(sameOrganizerConflict)
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El usuario "+username +
                    "no puede tener turnos que solapen en actividades del mismo organizador");

        var volunteering = volunteeringMapper.toEntity(user, activity, dto);
        var myVolunteering = volunteeringRepository.save(volunteering);
        return volunteeringMapper.toDto(myVolunteering);
    }

    private boolean sameActivityVolunteering(long activityId, List<VolunteeringRegisterDTO> list){
        return list.stream()
                .allMatch(dto -> dto.volunteeringData().activityId() == activityId);
    }

    private Activity getActivity(long id){
        return activityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La actividad con id "+id+" no se ha encontrado."));
    }
}
