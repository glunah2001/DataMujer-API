package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByIsFinalizedFalse();
}
