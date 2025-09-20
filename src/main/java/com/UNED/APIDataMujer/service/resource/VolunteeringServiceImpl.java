package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Volunteering;
import com.UNED.APIDataMujer.mapper.VolunteeringMapper;
import com.UNED.APIDataMujer.repository.VolunteeringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * Función de interfaz. Esta función se usa solo al crear una nueva actividad.
     * La persona que crea dicha actividad se le asigna el título de "Organizador"
     * de la actividad.
     * @param auth credenciales donde se extrae el usuario que creó la actividad
     * @param activity actividad registrada en la BD.
     * */
    @Override
    @Transactional
    public void insertOrganizerVolunteering(final Authentication auth, Activity activity) {
        final var user = userService.getMyUser(auth);
        var newVolunteering = volunteeringMapper.toEntity(user, activity);
        volunteeringRepository.save(newVolunteering);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * pendientes (de actividades sin finalizar) de la persona que solicita.
     * @param auth credenciales de autentificación del usuario
     * @return lista de voluntariados con detalles de la actividad
     * */
    @Override
    public List<VolunteeringDTO> getMyPendingVolunteering(Authentication auth) {
        var user = userService.getMyUser(auth);
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
}
