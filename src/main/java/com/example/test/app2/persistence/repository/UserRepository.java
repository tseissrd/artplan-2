package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tseissrd
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
	
    User findByUsername(String username);
    
}