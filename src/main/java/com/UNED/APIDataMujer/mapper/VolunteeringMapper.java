package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.entity.Volunteering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Clase encargada de mapear un DTO. A entidad Volunteering y viceversa.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class VolunteeringMapper {

    /**
     * Función encargada de mapear a una entidad Volunteering.
     * Se usa unidamente cuando se crea una nueva actividad, asignando al usuario creador
     * como "Organizador"
     * @param activity actividad en la cual la persona realza el voluntariado
     * @param user persona que se inscribe en la actividad
     * @return una entidad Volunteering lista para ser ingresada en BD
     * */
    public Volunteering toEntity(User user, Activity activity){
        return Volunteering.builder()
                .activity(activity)
                .user(user)
                .volunteerDate(LocalDate.from(activity.getStartDate()))
                .activityRole("Organizador")
                .build();
    }

    public Volunteering toEntity(User user, Activity activity, VolunteeringRegisterDTO dto){
        return Volunteering.builder()
                .activity(activity)
                .user(user)
                .volunteerDate(dto.volunteeringDate())
                .activityRole(dto.activityRole())
                .build();
    }

    /**
     * Función encargada de mapear un DTO. Desde una entidad.
     * @param volunteering entidad recuperada de la BD.
     * @return DTO. Volunteering
     * */
    public VolunteeringDTO toDto(Volunteering volunteering){
        return new VolunteeringDTO(
                volunteering.getId(),
                volunteering.getUser().getUsername(),
                volunteering.getActivity().getId(),
                volunteering.getActivity().getActivity(),
                volunteering.getActivity().getDescription(),
                volunteering.getActivity().getLocation(),
                volunteering.getActivity().isOnSite(),
                volunteering.getVolunteerDate(),
                volunteering.getActivityRole()
        );
    }

}
