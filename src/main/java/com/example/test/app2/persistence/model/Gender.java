package com.example.test.app2.persistence.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author Sovereign
 */
@Entity
public class Gender
{
  
  public static enum Instance {
    MALE,
    FEMALE;
  }
  
  private static final Map<Instance, Gender> instances = new HashMap<>();
  
//  @Id
//  @GeneratedValue(strategy=GenerationType.IDENTITY)
//  private long id;
  
  @Id
  private String name;
  
  protected Gender() {}
  
  public Gender(Instance value) {
    if (instances.containsKey(value))
      throw new DuplicateEntryCreationAttemptException();
        
    this.name = value.toString();
  }
  
  public static Gender getFor(Instance value) {
    if (Objects.isNull(value))
      return null;
    
    if (!instances.containsKey(value))
      instances.put(value, new Gender(value));
    
    return instances.get(value);
  }
  
  public static Gender getFor(String name) {
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