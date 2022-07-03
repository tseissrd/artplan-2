package com.example.test.app2.persistence.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author tseissrd
 */
@Entity
public class Species
{
  
  public static enum Instance {
    CAT,
    DOG,
    GIRAFFE;
  }
  
  private static final Map<Instance, Species> instances = new HashMap<>();
  
  @Id
  private String name;
  
  protected Species() {}
  
  public Species(Instance value) {
    if (instances.containsKey(value))
      throw new DuplicateEntryCreationAttemptException();
        
    this.name = value.toString();
  }
  
  public static Species getFor(Instance value) {
    if (Objects.isNull(value))
      return null;
    
    if (!instances.containsKey(value))
      instances.put(value, new Species(value));
    
    return instances.get(value);
  }
  
  public static Species getFor(String name) {
    if (
      Objects.nonNull(
        Instance.valueOf(name)
      )
    )
      return getFor(Instance.valueOf(name));
    else
      return null;
  }
  
  @Override
  public String toString() {
    return getName();
  }
  
  public String getName() {
    return this.name;
  }
}