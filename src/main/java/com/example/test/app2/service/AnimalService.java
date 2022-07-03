package com.example.test.app2.service;

import com.example.test.app2.persistence.model.Animal;
import com.example.test.app2.persistence.model.Gender;
import com.example.test.app2.persistence.model.Species;
import com.example.test.app2.persistence.repository.AnimalRepository;
import com.example.test.app2.persistence.repository.GenderRepository;
import com.example.test.app2.persistence.repository.SpeciesRepository;
import java.util.Date;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anechka
 */
@Service
public final class AnimalService {
  
  public static class NameUsedException
  extends Exception {
    public NameUsedException(String message) {
      super(message);
    }
  }
  
  public static class NoAnimalFoundException
  extends Exception {
    public NoAnimalFoundException(String message) {
      super(message);
    }
  }
  
  @Autowired
  private AnimalRepository animalRepo;
  
  @Autowired
  private SpeciesRepository speciesRepo;
  
  @Autowired
  private GenderRepository genderRepo;
  
  private AnimalService() {}
  
  public long createAnimal(
    String name,
    Species species,
    Date birthdate,
    Gender gender
  ) throws NameUsedException {
    if (
      Objects.nonNull(
        animalRepo.findByName(name)
      )
    )
      throw new NameUsedException("Animal with name " + name + " exists");
    
    Animal animal = animalRepo.save(
      new Animal(name, species, birthdate, gender)
    );
    
    return animal.getId();
  }
  
  public Animal getAnimal(long id) throws NoAnimalFoundException {
    return animalRepo.findById(id)
      .orElseThrow(
        () -> new NoAnimalFoundException("could not find animal by id: " + id)
      );
  }
  
  public void editAnimal(
    Animal animal,
    String name,
    Species species,
    Date birthdate,
    Gender gender
  ) throws NameUsedException, NoAnimalFoundException {
    if (
      Objects.nonNull(
        animalRepo.findByName(name)
      )
    )
      throw new NameUsedException("Animal with name " + name + " exists");
    
    if (Objects.nonNull(name))
      animal.setName(name);
    if (Objects.nonNull(birthdate))
      animal.setBirthdate(birthdate);
    if (Objects.nonNull(gender))
      animal.setGender(gender);
    if (Objects.nonNull(species))
      animal.setSpecies(species);
    
    animalRepo.save(animal);
  }
  
  public void editAnimal(
    long id,
    String name,
    Species species,
    Date birthdate,
    Gender gender
  ) throws NameUsedException, NoAnimalFoundException {
    
    Animal animal = animalRepo.findById(id)
      .orElseThrow(
        () -> new NoAnimalFoundException("could not find animal by id: " + id)
      );
    
    editAnimal(
      animal,
      name,
      species,
      birthdate,
      gender
    );
  }
  
  public void removeAnimal(long id)
  throws NoAnimalFoundException {
    Animal animal = animalRepo.findById(id)
      .orElseThrow(
        () -> new NoAnimalFoundException("could not find animal by id: " + id)
      );
    
    animalRepo.delete(animal);
  }
  
}
