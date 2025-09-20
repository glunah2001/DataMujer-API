package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.LegalPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalPersonRepository extends JpaRepository<LegalPerson, Long> {
}
