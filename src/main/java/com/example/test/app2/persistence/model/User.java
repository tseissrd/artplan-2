package com.example.test.app2.persistence.model;

import java.sql.Timestamp;
import java.time.Instant;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import static javax.persistence.FetchType.EAGER;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author Sovereign
 */
@Entity
@Table(name="AppUser")
public class User
implements UserDetails
{
  @Id
  @NotEmpty
  private String username;

  private String password;
  
  @ManyToMany(fetch = EAGER)
  private final Set<Role> roles;
  
  @OneToMany(fetch = EAGER)
  private final Set<Token> tokens;
  
  @OneToMany(fetch = EAGER)
  private final Set<Animal> animals;
  
  private Timestamp firstLoginFailure;
  
  private byte loginFailures;
  
  protected User() {
    this.tokens = new HashSet<>();
    this.animals = new HashSet<>();
    this.roles = new HashSet<>();
  }

  public User(String name, String password) {
    this.username = name;
    this.password = password;
    this.tokens = new HashSet<>();
    this.animals = new HashSet<>();
    this.roles = new HashSet<>();
  }

  @Override
  public String toString() {
    return this.username;
  }

  @Override
  public String getUsername() {
    return this.username;
  }
  
  public Set<Role> getRoles() {
    return roles;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRoles();
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
  
  public void addToken(Token token) {
    tokens.add(token);
  }
  
  public boolean checkToken(Token token) {
    return tokens.contains(token);
  }
  
  public void removeToken(Token token) {
    tokens.remove(token);
  }
  
  public Token removeRandomToken() {
    Token tokenToRemove = (Token)tokens.toArray()[0];
    
    tokens.remove(tokenToRemove);
    
    return tokenToRemove;
  }
  
  public int getNumberOfTokens() {
    return tokens.size();
  }
  
  public Timestamp getFirstLoginFailure() {
    return this.firstLoginFailure;
  }
  
  public void setFirstLoginFailure(Timestamp time) {
    this.firstLoginFailure = time;
  }
  
  public byte getLoginFailures() {
    return this.loginFailures;
  }
  
  public void incrementLoginFailures() {
    this.loginFailures += 1;
    if (
      Objects.isNull(this.firstLoginFailure)
    )
      this.firstLoginFailure = Timestamp.from(
        Instant.now()
      );
  }
  
  public void resetLoginFailures() {
    this.loginFailures = 0;
    this.firstLoginFailure = null;
  }
  
  public void addAnimal(Animal animal) {
    this.animals.add(animal);;
  }
  
  public void removeAnimal(Animal animal) {
    this.animals.remove(animal);
  }
  
  public Set<Animal> getAnimals() {
    return Collections.unmodifiableSet(this.animals);
  }
  
  public boolean isAnimalOwned(Animal animal) {
    return this.animals.contains(animal);
  }
    
}