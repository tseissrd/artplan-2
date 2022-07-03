package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.Gender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Sovereign
 */
@Repository
public interface GenderRepository extends JpaRepository<Gender, Long> {
    
  Gender findByName(String name);
  
}