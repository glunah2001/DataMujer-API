package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Volunteering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteeringRepository extends JpaRepository<Volunteering, Long> {
    List<Volunteering> findByUserIdAndActivityIsFinalizedFalse(Long userId);
    List<Volunteering> findByActivityId(Long id);

    @Query("""
        SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
        FROM Volunteering v
        WHERE v.user.id = :userId
          AND v.isMainOrganizer = true
          AND v.activity.isFinalized = false
          AND v.activity.startDate < :endShift
          AND v.activity.endDate > :startShift
    """)
    boolean existsOrganizerConflict(@Param("userId") Long userId,
                                    @Param("startShift") LocalDateTime startShift,
                                    @Param("endShift") LocalDateTime endShift);

    @Query("""
    SELECT CASE WHEN COUNT(v) > 0 THEN true ELSE false END
    FROM Volunteering v
    JOIN v.activity a
    JOIN Volunteering org ON org.activity.id = a.id AND org.isMainOrganizer = true
    WHERE v.user.id = :userId
      AND v.activity.isFinalized = false
      AND v.startShift < :endShift
      AND v.endShift > :startShift
      AND org.user.id = :organizerId
    """)
    boolean existsConflictWithSameOrganizer(@Param("userId") Long userId,
                                            @Param("organizerId") Long organizerId,
                                            @Param("newActivityId") Long newActivityId,
                                            @Param("startShift") LocalDateTime startShift,
                                            @Param("endShift") LocalDateTime endShift);

    @Query("""
    SELECT v.user.id
    FROM Volunteering v
    WHERE v.activity.id = :activityId
      AND v.isMainOrganizer = true
    """)
    Optional<Long> findOrganizerIdByActivityId(@Param("activityId") Long activityId);
}
