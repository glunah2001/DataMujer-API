package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.response.ParticipationDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Participation;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.ParticipationState;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ParticipationMapper {

    public Participation toEntity(User user, Activity activity){
        return Participation.builder()
                .activity(activity)
                .user(user)
                .registrationDate(LocalDate.now())
                .status(ParticipationState.PENDIENTE)
                .build();
    }

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
