package com.example.test.app2.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;

/**
 *
 * @author Sovereign
 */
@Entity
public class Animal
{

  protected Animal() {
  }

  public Animal(
    String name,
    Species species,
    Date birthdate,
    Gender gender
  ) {
    this.name = name;
    this.gender = gender;
    this.species = species;
    this.birthdate = birthdate;
  }

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private long id;
  
  @Column(unique=true)
  private String name;

  @ManyToOne
  private Gender gender;
  
  @ManyToOne
  private Species species;
  
  private Date birthdate;

  @Override
  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public long getId() {
    return this.id;
  }
  
  public Species getSpecies() {
    return this.species;
  }
  
  public void setSpecies(Species species) {
    this.species = species;
  }
  
  public Gender getGender() {
    return this.gender;
  }
  
  public void setGender(Gender gender) {
    this.gender = gender;
  }
  
  public Date getBirthdate() {
    return this.birthdate;
  }
  
  public void setBirthdate(Date birthdate) {
    this.birthdate = birthdate;
  }
}