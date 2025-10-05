package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.response.ParticipationDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Participation;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.ParticipationState;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Clase encargada del mapeo de participaciones.
 * @author glunah2001
 * */
@Component
public class ParticipationMapper {

    /**
     * Mapeo de una entidad con base en información proporcionada.
     * @param user usuario encargado de la participación.
     * @param activity actividad en la que participa.
     * @return entidad creada de tipo Participation.
     * */
    public Participation toEntity(User user, Activity activity){
        return Participation.builder()
                .activity(activity)
                .user(user)
                .registrationDate(LocalDate.now())
                .status(ParticipationState.PENDIENTE)
                .build();
    }

    /**
     * Mapeo de una entidad participación a un dto.
     * @param participation entidad participation recuperada de la base de datos.
     * @return dto. Con la información de la participación.
     * */
    public ParticipationDTO toDto(Participation participation){
        return new ParticipationDTO(
                participation.getId(),
                participation.getRegistrationDate(),
                participation.getStartDate(),
                participation.getEndDate(),
                participation.getUser().getUsername(),
                participation.getActivity().getId(),
                participation.getActivity().getActivity(),
                participation.getActivity().getDescription(),
                participation.getActivity().getLocation(),
                participation.getActivity().isOnSite(),
                participation.getActivity().getStartDate(),
                participation.getActivity().getEndDate(),
                participation.getStatus()
                );
    }

}
