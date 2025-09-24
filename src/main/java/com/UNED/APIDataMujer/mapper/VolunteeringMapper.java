package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.entity.Volunteering;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase encargada de mapear un DTO. A entidad Volunteering y viceversa.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class VolunteeringMapper {

    public Volunteering toEntity(User user, Activity activity){
        return Volunteering.builder()
                .activity(activity)
                .user(user)
                .startShift(activity.getStartDate())
                .endShift(activity.getEndDate())
                .activityRole("Organizador Principal")
                .isMainOrganizer(true)
                .build();
    }

    public Volunteering toEntity(User user,
                                 Activity activity,
                                 VolunteeringRegisterDTO dto){
        return Volunteering.builder()
                .activity(activity)
                .user(user)
                .startShift(dto.startShift())
                .endShift(dto.endShift())
                .activityRole(dto.activityRole())
                .isMainOrganizer(false)
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
                volunteering.getStartShift(),
                volunteering.getEndShift(),
                volunteering.getActivityRole()
        );
    }

}
