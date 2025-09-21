package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.PhysicalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicalPersonRepository extends JpaRepository<PhysicalPerson, Long> {
    Optional<PhysicalPerson> findByNationalId(String nationalId);
    List<PhysicalPerson> findByNameContainingIgnoreCase(String name);
    List<PhysicalPerson> findByFirstSurnameContainingIgnoreCase(String surname);
    List<PhysicalPerson> findBySecondSurnameContainingIgnoreCase(String surname);
}
