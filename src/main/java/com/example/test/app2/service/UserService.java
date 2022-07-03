package com.example.test.app2.service;

import com.example.test.app2.persistence.model.Animal;
import com.example.test.app2.persistence.model.Token;
import com.example.test.app2.persistence.model.User;
import com.example.test.app2.persistence.repository.RoleRepository;
import com.example.test.app2.persistence.repository.TokenRepository;
import com.example.test.app2.persistence.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anechka
 */
@Service
public class UserService
implements UserDetailsService, AuthenticationManager {
  
  private static final byte MAX_LOGIN_ATTEMPTS = (byte)10;
  private static final Duration ACCOUNT_LOCK_TIME = Duration.of(1, ChronoUnit.HOURS);
  private static final int MAX_NUMBER_OF_TOKENS = 10;
  
  public static class UserExistsException
  extends Exception {
    public UserExistsException() {
      super();
    }
    
    public UserExistsException(String msg) {
      super(msg);
    }
  }
  
  public static class AnimalNotOwnedException
  extends Exception {
    public AnimalNotOwnedException() {
      super();
    }
    
    public AnimalNotOwnedException(String msg) {
      super(msg);
    }
  }
  
  public static class TokenNotOwnedException
  extends Exception {
    public TokenNotOwnedException() {
      super();
    }
    
    public TokenNotOwnedException(String msg) {
      super(msg);
    }
  }
  
  @Autowired
  private UserRepository userRepo;
  
  @Autowired
  private TokenRepository tokenRepo;
  
  @Autowired
  private RoleRepository roleRepo;
  
  @Autowired
  private AnimalService animalSvc;
  
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepo.findByUsername(username);
  }
  
  public void register(String username, String password) throws UserExistsException {
    if (Objects.nonNull(userRepo.findByUsername(username)))
      throw new UserExistsException("There is already user with name: " + username);
    
    User user = new User(
      username,
      BCrypt.hashpw(
        password,
        BCrypt.gensalt()
      )
    );
    
    userRepo.save(user);
  }
  
  public Token issueToken(User user) {
    if (user.getNumberOfTokens() >= MAX_NUMBER_OF_TOKENS) {
      Token removedToken = user.removeRandomToken();
      user = userRepo.save(user);
      tokenRepo.delete(removedToken);
    }
    
    Token token = new Token();
    user.addToken(token);
    
    token = tokenRepo.save(token);
    userRepo.save(user);
    
    return token;
  }
  
  public void removeToken(User user, Token token)
  throws TokenNotOwnedException {
    if (!user.checkToken(token))
      throw new TokenNotOwnedException("token " + token.toString() + " is not owned by user: " + user.getUsername());
    
    user.removeToken(token);
    userRepo.save(user);
    tokenRepo.delete(token);
  }
  
  @Override
  public Authentication authenticate(Authentication authentication)
  throws AuthenticationException {
    String username = authentication.getName();
    String password = (String)authentication.getCredentials();

    User user = userRepo.findByUsername(username);
    if (Objects.isNull(user))
      throw new BadCredentialsException("user not found: " + username);

    if (
      Objects.nonNull(
        user.getFirstLoginFailure()
      )
      && user.getFirstLoginFailure()
        .toInstant()
        .isBefore(
          Instant.now()
            .minus(ACCOUNT_LOCK_TIME)
        )
    ) {
      user.resetLoginFailures();
      userRepo.save(user);
    }  

    if (user.getLoginFailures() >= MAX_LOGIN_ATTEMPTS)
      throw new LockedException("too many login attempts for user: " + username);

    if (
      BCrypt.checkpw(
        password,
        user.getPassword()
      )
    ) {
      user.resetLoginFailures();
      user = userRepo.save(user);

      return new UsernamePasswordAuthenticationToken(
        username,
        issueToken(user)
          .getUuid(),
        user.getRoles()
      );
    } else if (
      user.checkToken(
        tokenRepo.findByUuid(password)
      )
    ) {
      user.resetLoginFailures();
      userRepo.save(user);

      return new UsernamePasswordAuthenticationToken(
        username,
        password,
        user.getRoles()
      );
    } else {
      user.incrementLoginFailures();
      userRepo.save(user);

      throw new BadCredentialsException("password does not match for user: " + username);
    }
  }
  
  public Authentication authenticate(String username, String password)
  throws AuthenticationException {
    Authentication session = new PreAuthenticatedAuthenticationToken(
      username,
      password
    );

    Authentication authSession = authenticate(session);
    if (!authSession.isAuthenticated())
      throw new BadCredentialsException("Authentication failure for user " + username);
    else
      return authSession;
  }
  
  public boolean checkUsername(String username) {
    return userRepo.existsById(username);
  }
  
  public long[] getAnimals(User user) {
    return user.getAnimals()
      .stream()
      .mapToLong(animal -> animal.getId())
      .toArray();
  }
  
  public long[] getAnimals(String username) {
    User user = userRepo.findByUsername(username);
    
    if (Objects.isNull(user))
      throw new UsernameNotFoundException("could not find user for username: " + username);
    
    return getAnimals(user);
  }
  
  public void assignAnimal(User user, Animal animal) {
    user.addAnimal(animal);
    userRepo.save(user);
  }
  
  public void assignAnimal(String username, Animal animal)
  throws UsernameNotFoundException {
    User user = userRepo.findByUsername(username);
    
    if (Objects.isNull(user))
      throw new UsernameNotFoundException("could not find user for username: " + username);
    
    assignAnimal(user, animal);
  }
  
  public void assignAnimal(String username, long animalId)
  throws AnimalService.NoAnimalFoundException {
    assignAnimal(
      username,
      animalSvc.getAnimal(animalId)
    );
  }
  
  public boolean isAnimalOwned(User user, Animal animal) {
    return user.isAnimalOwned(animal);
  }
  
  public boolean isAnimalOwned(String username, Animal animal) {
    User user = userRepo.findByUsername(username);
    
    if (Objects.isNull(user))
      throw new UsernameNotFoundException("could not find user for username: " + username);
    
    return isAnimalOwned(user, animal);
  }
  
  public boolean isAnimalOwned(String username, long animalId)
  throws AnimalService.NoAnimalFoundException {
    Animal animal = animalSvc.getAnimal(animalId);
    
    return isAnimalOwned(username, animal);
  }
  
  public void unassignAnimal(User user, Animal animal)
  throws AnimalNotOwnedException {
    if (!user.isAnimalOwned(animal))
      throw new AnimalNotOwnedException("animal " + animal.getId() + " is not owned by user: " + user.getUsername());
    
    user.removeAnimal(animal);
    userRepo.save(user);
  }
  
  public void unassignAnimal(String username, Animal animal)
  throws UsernameNotFoundException, AnimalNotOwnedException {
    User user = userRepo.findByUsername(username);
    
    if (Objects.isNull(user))
      throw new UsernameNotFoundException("could not find user for username: " + username);
    
    unassignAnimal(user, animal);
  }
  
  public void unassignAnimal(String username, long animalId)
  throws 
    AnimalService.NoAnimalFoundException,
    UsernameNotFoundException,
    AnimalNotOwnedException {
    unassignAnimal(
      username,
      animalSvc.getAnimal(animalId)
    );
  }
  
}
