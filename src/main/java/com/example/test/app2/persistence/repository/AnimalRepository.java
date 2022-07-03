package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.Animal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tseissrd
 */
@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {
	
    Animal findByName(String name);
    
}