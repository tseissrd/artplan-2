package com.example.test.app2.persistence.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author Sovereign
 */
@Entity
@Table(name="AppRole")
public class Role
implements GrantedAuthority
{

  @Id
  @NotEmpty
  private String name;
 
  
  protected Role() {
  }

  public Role(String name) {
    this.name = name;
    
  }

  @Override
  public String toString() {
    return this.name;
  }

  public String getName() {
    return this.name;
  }

  @Override
  public String getAuthority() {
    return this.name;
  }
    
}