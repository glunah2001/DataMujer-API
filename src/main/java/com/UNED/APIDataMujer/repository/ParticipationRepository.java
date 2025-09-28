package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Participation;
import com.UNED.APIDataMujer.enums.ParticipationState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    Page<Participation> findByUserIdAndActivityIsFinalizedFalseAndStatusNot(Long userid,
                                                                            ParticipationState excludedState,
                                                                            Pageable pageable);
}
