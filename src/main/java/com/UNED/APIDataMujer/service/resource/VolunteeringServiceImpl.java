package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Volunteering;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.VolunteeringMapper;
import com.UNED.APIDataMujer.repository.VolunteeringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ActivityService activityService;

    private final VolunteeringMapper volunteeringMapper;
    private final VolunteeringRepository volunteeringRepository;

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
    public List<VolunteeringDTO> getMyPendingVolunteering(Authentication auth) {
        final var user = userService.findMyUser(auth);
        var volunteering = volunteeringRepository.findByUserIdAndActivityIsFinalizedFalse(user.getId());
        return toDtoStreamConverter(volunteering);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * de una actividad.
     * @param id id de la actividad a buscar
     * @return lista de voluntariados con detalles de la actividad.
     * */
    @Override
    public List<VolunteeringDTO> getVolunteeringForAnActivity(long id) {
        var volunteering = volunteeringRepository.findByActivityId(id);
        return toDtoStreamConverter(volunteering);
    }

    @Override
    @Transactional
    public long insertVolunteering(VolunteeringWrapperDTO dto) {
        var list = dto.volunteering();
        if(list.isEmpty())
            throw new IllegalArgumentException("Información de voluntariados vacía");

        var activityId = dto.activityId();
        if(!sameActivityVolunteering(activityId, list))
            throw new IllegalArgumentException(("Inconsistencia de datos detectada: Voluntariados para " +
                    "diferentes actividades."));

        list.forEach(this::insertVolunteering);
        return activityId;
    }

    @Override
    @Transactional
    public VolunteeringDTO insertVolunteering(VolunteeringRegisterDTO dto) {
        final var activity = getActivity(dto.activityId());
        var username = dto.username();

        if(activity.isFinalized())
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" se está registrando en una actividad clausurada.");

        if(!dto.startShift().isBefore(dto.endShift()))
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" indica que la fecha de inicio está después de la fecha de " +
                    "cierre de su turno.");

        if(dto.startShift().isBefore(activity.getStartDate()) ||
           dto.endShift().isAfter(activity.getEndDate()))
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" se está registrando fuera del rango de la actividad.");

        var shiftLength = ChronoUnit.HOURS.between(dto.startShift(), dto.endShift());
        if(shiftLength < 1 || shiftLength> 8)
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El voluntariado " +
                    "del usuario "+username+" debe abarcar una cantidad de horas razonables. " +
                    "De 1 a 8 horas por turno.");

        final var user = userService.findUserByUsername(username);
        var organizerId = volunteeringRepository.findOrganizerIdByActivityId(activity.getId())
                .orElseThrow(() -> new IllegalArgumentException("Ocurrió un error al consultar organizador " +
                        "de la actividad"));

        var sameOrganizerConflict = volunteeringRepository.existsConflictWithSameOrganizer(
                user.getId(), organizerId, activity.getId(), dto.startShift(), dto.endShift());

        if(sameOrganizerConflict)
            throw new IllegalArgumentException("Inconsistencia de datos detectada: El usuario "+username +
                    "no puede tener turnos que solapen en actividades del mismo organizador");

        var volunteering = volunteeringMapper.toEntity(user, activity, dto);
        var myVolunteering = volunteeringRepository.save(volunteering);
        return volunteeringMapper.toDto(myVolunteering);
    }

    /**
     * Función auxiliar encargada de llamar al mapper de voluntariados para convertir
     * entidades a DTO. Usando streams.
     * @param volunteering lista de entidades volunteering
     * @return lista de DTO. Volunteering
     * */
    private List<VolunteeringDTO> toDtoStreamConverter(List<Volunteering> volunteering){
        return volunteering.stream()
                .map(volunteeringMapper::toDto)
                .toList();
    }

    private boolean sameActivityVolunteering(long activityId, List<VolunteeringRegisterDTO> list){
        return list.stream()
                .allMatch(dto -> dto.activityId() == activityId);
    }

    private Activity getActivity(long id){
        return activityService.getActivity(id);
    }
}
