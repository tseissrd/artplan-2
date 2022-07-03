package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tseissrd
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    
}