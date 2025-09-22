package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Volunteering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VolunteeringRepository extends JpaRepository<Volunteering, Long> {
    List<Volunteering> findByUserIdAndActivityIsFinalizedFalse(Long userId);
    List<Volunteering> findByActivityId(Long id);

    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
        FROM Volunteering v
        WHERE v.user.id = :userId
          AND v.activity.isFinalized = false
          AND NOT (v.endShift <= :startShift OR v.startShift >= :endShift)
    """)
    boolean existsByUserAndOverlappingShift(
            @Param("userId") Long userId,
            @Param("startShift") LocalDateTime startShift,
            @Param("endShift") LocalDateTime endShift
    );
}
