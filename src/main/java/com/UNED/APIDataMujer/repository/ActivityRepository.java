package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findByIsFinalizedFalse(Pageable pageable);
}
