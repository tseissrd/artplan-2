package com.example.test.app2.persistence.repository;

import com.example.test.app2.persistence.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author tseissrd
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
  
  Token findByUuid(String uuid);
  
}