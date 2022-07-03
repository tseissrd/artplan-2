package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.Species;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tseissrd
 */
@Repository
public interface SpeciesRepository extends JpaRepository<Species, Long> {

  Species findByName(String name);
  
}