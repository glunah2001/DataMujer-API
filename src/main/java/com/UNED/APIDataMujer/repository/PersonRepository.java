package com.UNED.APIDataMujer.repository;

import com.UNED.APIDataMujer.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
