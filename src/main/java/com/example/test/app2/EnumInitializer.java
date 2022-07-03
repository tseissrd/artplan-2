/*
 */
package com.example.test.app2;

import com.example.test.app2.persistence.model.Gender;
import com.example.test.app2.persistence.model.Species;
import com.example.test.app2.persistence.repository.GenderRepository;
import com.example.test.app2.persistence.repository.SpeciesRepository;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Sovereign
 */
@Configuration
public class EnumInitializer {
  
  @Autowired
  private GenderRepository genderRepo;
  
  @Autowired
  private SpeciesRepository speciesRepo;
  
  @Bean
  public void initializeEnums() {
    List<Gender> genders = Arrays.asList(
      Gender.Instance
        .values()
    ).stream()
      .map(instance -> Gender.getFor(instance))
      .toList();
    
    genderRepo.saveAll(genders);
    
    List<Species> species = Arrays.asList(
      Species.Instance
        .values()
    ).stream()
      .map(instance -> Species.getFor(instance))
      .toList();
    
    speciesRepo.saveAll(species);
  }
  
}
