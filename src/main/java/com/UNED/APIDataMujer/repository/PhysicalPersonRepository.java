package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.PhysicalPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicalPersonRepository extends JpaRepository<PhysicalPerson, Long> {
    Optional<PhysicalPerson> findByNationalId(String nationalId);
    Page<PhysicalPerson> findByNameContainingIgnoreCase(String name, Pageable pageable);
    @Query("""
        SELECT p FROM PhysicalPerson p
        WHERE LOWER(p.firstSurname) LIKE LOWER(CONCAT('%', :surname, '%'))
           OR LOWER(p.secondSurname) LIKE LOWER(CONCAT('%', :surname, '%'))
    """)
    Page<PhysicalPerson> findByAnySurnameContainingIgnoreCase(@Param("surname") String surname, Pageable pageable);
}
