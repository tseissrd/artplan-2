package com.example.test.app2.persistence.model;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

/**
 *
 * @author tseissrd
 */
@Entity
public class Token
{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  
  private final String uuid;

  public Token() {
    this.uuid = UUID.randomUUID()
      .toString();
  }
  
  public String getUuid() {
    return this.uuid;
  }

  @Override
  public String toString() {
    return this.uuid;
  }
  
  @Override
  public boolean equals(Object other) {
    if (this == other)
      return true;
    
    if (!(other instanceof Token))
      return false;
    
    return getUuid()
      .equals(
        ((Token)other).getUuid()
      );
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 47 * hash + (int) (this.id ^ (this.id >>> 32));
    hash = 47 * hash + Objects.hashCode(this.uuid);
    return hash;
  }
    
}