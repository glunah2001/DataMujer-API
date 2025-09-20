package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Clase encargada de convertir un DTO. A una entidad Activity y viceversa
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class ActivityMapper {

    /**
     * Función principal. Transforma el DTO. En la entidad sin registrar en la BD
     * @
     * */
    public Activity toEntity(ActivityRegisterDTO dto){
        return Activity.builder()
                .activity(dto.activity())
                .description(dto.description())
                .location(dto.location())
                .isOnSite(dto.isOnSite())
                .isFinalized(false)
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .build();
    }

    public ActivityDTO toDto(Activity activity){
        return new ActivityDTO(
                activity.getId(),
                activity.getActivity(),
                activity.getDescription(),
                activity.getLocation(),
                activity.isOnSite(),
                activity.getStartDate(),
                activity.getEndDate(),
                activity.isFinalized()
        );
    }

}
