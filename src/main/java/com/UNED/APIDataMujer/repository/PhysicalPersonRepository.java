package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.PhysicalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhysicalPersonRepository extends JpaRepository<PhysicalPerson, Long> {
}
