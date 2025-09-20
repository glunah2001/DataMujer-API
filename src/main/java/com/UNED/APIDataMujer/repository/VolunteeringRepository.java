package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Volunteering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteeringRepository extends JpaRepository<Volunteering, Long> {
    List<Volunteering> findByUserIdAndActivityIsFinalizedFalse(Long userId);
    List<Volunteering> findByActivityId(Long id);
}
