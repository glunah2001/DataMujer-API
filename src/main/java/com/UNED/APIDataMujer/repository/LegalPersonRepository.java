package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.LegalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegalPersonRepository extends JpaRepository<LegalPerson, Long> {
    Optional<LegalPerson> findByLegalId(String legalId);
    List<LegalPerson> findByBusinessNameContainingIgnoreCase(String businessName);
}
