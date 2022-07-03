package com.example.test.app2.routers;

import com.example.test.app2.persistence.model.Animal;
import com.example.test.app2.persistence.model.Gender;
import com.example.test.app2.persistence.model.Species;
import com.example.test.app2.routers.error.AnimalServiceException;
import com.example.test.app2.routers.error.ServiceException;
import com.example.test.app2.service.AnimalService;
import com.example.test.app2.service.UserService;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.function.RouterFunction;
import static org.springframework.web.servlet.function.RouterFunctions.*;
import org.springframework.web.servlet.function.ServerResponse;

/**
 *
 * @author tseissrd
 */
@Configuration
public class AnimalRouter {
  
  @Autowired
  private AnimalService animalSvc;
  
  @Autowired
  private UserService userSvc;
  
  @Bean
  public RouterFunction<ServerResponse> getAnimalRouter() {
    return route()
      .GET("/animal/get", req -> {
        Authentication session = SecurityContextHolder.getContext()
          .getAuthentication();
        
        if (Objects.isNull(session)) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .USER_UNAUTHORIZED
          );
          
          return ServerResponse.status(HttpStatus.UNAUTHORIZED)
            .body(
              ex.toBody()
            );
        }
        
        Map data = req.body(Map.class);
        
        if (Objects.isNull(data))
          data = Map.of();
        
        if (!data.containsKey("id")) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .REQUIRED_PARAMETERS_MISSING,
            "id, user(username, id)"
          );

          return ServerResponse.badRequest()
            .body(
              ex.toBody()
            );
        }

        long id = ((Number)data.get("id"))
          .longValue();

        Animal animal;

        try {
          animal = animalSvc.getAnimal(id);
        } catch (AnimalService.NoAnimalFoundException ogEx) {
          ServiceException ex = new AnimalServiceException(
            AnimalServiceException.Type
              .COULD_NOT_FIND_ANIMAL_FOR_ID,
            Long.toString(id)
          );

          return ServerResponse.status(HttpStatus.NOT_FOUND)
            .body(
              ex.toBody()
            );
        }

        return ServerResponse.ok()
          .body(
            Map.of(
              "status", "ok",
              "animal", Map.of(
                "id", animal.getId(),
                "name", animal.getName(),
                "birthdate", animal.getBirthdate()
                  .toInstant()
                  .toString(),
                "species", animal.getSpecies()
                  .getName(),
                "gender", animal.getGender()
                  .getName()
              )
            )
          );
      }).GET("/animal/list", req -> {
        Authentication session = SecurityContextHolder.getContext()
          .getAuthentication();
        
        if (Objects.isNull(session)) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .USER_UNAUTHORIZED
          );
          
          return ServerResponse.status(HttpStatus.UNAUTHORIZED)
            .body(
              ex.toBody()
            );
        }
          
        return ServerResponse.ok()
          .body(
            Map.of(
              "status", "ok",
              "animals", userSvc.getAnimals(
                (String)session.getPrincipal()
              )
            )
          );
      }).POST("/animal/create", req -> {
        Authentication session = SecurityContextHolder.getContext()
          .getAuthentication();
        
        if (Objects.isNull(session)) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .USER_UNAUTHORIZED
          );
          
          return ServerResponse.status(HttpStatus.UNAUTHORIZED)
            .body(
              ex.toBody()
            );
        }  
        
        Map data = req.body(Map.class);
        
        if (Objects.isNull(data))
          data = Map.of();
        
        String name = (String)data.get("name");
        
        Species species = null;
        if (data.containsKey("species"))
          species = Species.getFor(
            ((String)data.get("species"))
              .toUpperCase()
          );
        
        Date birthdate = null;
        if (data.containsKey("birthdate"))
          birthdate = Date.from(
            Instant.parse(
              (String)data.get("birthdate")
            )
          );

        Gender gender = null;
        if (data.containsKey("gender"))
          gender = Gender.getFor(
            ((String)data.get("gender"))
              .toUpperCase()
          );

        if (
          Objects.isNull(name)
          || Objects.isNull(species)
          || Objects.isNull(birthdate)
          || Objects.isNull(gender)
        ) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .REQUIRED_PARAMETERS_MISSING,
            "name, species, birthdate, gender"
          );

          return ServerResponse.badRequest()
            .body(
              ex.toBody()
            );
        }

        long id;

        try {
          id = animalSvc.createAnimal(name, species, birthdate, gender);
          userSvc.assignAnimal(
            (String)session.getPrincipal(),
            id
          );
        } catch (AnimalService.NameUsedException ogEx) {
          ServiceException ex = new AnimalServiceException(
            AnimalServiceException.Type
              .NAME_USED,
            name
          );

          return ServerResponse.status(HttpStatus.CONFLICT)
            .body(
              ex.toBody()
            );
        }

        return ServerResponse.ok()
          .body(
            Map.of(
              "status", "ok",
              "id", id
            )
          );
      }).POST("/animal/update", req -> {
        Authentication session = SecurityContextHolder.getContext()
          .getAuthentication();
        
        if (Objects.isNull(session)) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .USER_UNAUTHORIZED
          );
          
          return ServerResponse.status(HttpStatus.UNAUTHORIZED)
            .body(
              ex.toBody()
            );
        }
        
        String username = (String)session.getPrincipal();
        
        Map data = req.body(Map.class);
        
        if (Objects.isNull(data))
          data = Map.of();
        
        Number idParameter = (Number)data.get("id");
        String name = (String)data.get("name");
        
        Species species = null;
        if (data.containsKey("species"))
          species = Species.getFor(
            ((String)data.get("species"))
              .toUpperCase()
          );
        
        Date birthdate = null;
        if (data.containsKey("birthdate"))
          birthdate = Date.from(
            Instant.parse(
              (String)data.get("birthdate")
            )
          );

        Gender gender = null;
        if (data.containsKey("gender"))
          gender = Gender.getFor(
            ((String)data.get("gender"))
              .toUpperCase()
          );

        if (
          Objects.isNull(idParameter)
          || (
            Objects.isNull(name)
            && Objects.isNull(species)
            && Objects.isNull(birthdate)
            && Objects.isNull(gender)
          )
        ) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .REQUIRED_PARAMETERS_MISSING,
            "id and either name or species or birthdate or gender"
          );

          return ServerResponse.badRequest()
            .body(
              ex.toBody()
            );
        }
        
        long id = idParameter.longValue();
        
        try {
          if (!userSvc.isAnimalOwned(username, id)) {
            ServiceException ex = new AnimalServiceException(
              AnimalServiceException.Type
                .ANIMAL_NOT_OWNED,
              "animal " + id + " is not owned by user: " + username
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }
            
          animalSvc.editAnimal(id, name, species, birthdate, gender);
        } catch (AnimalService.NameUsedException ogEx) {
          ServiceException ex = new AnimalServiceException(
            AnimalServiceException.Type
              .NAME_USED,
            name
          );

          return ServerResponse.status(HttpStatus.CONFLICT)
            .body(
              ex.toBody()
            );
        } catch (AnimalService.NoAnimalFoundException ogEx) {
          ServiceException ex = new AnimalServiceException(
            AnimalServiceException.Type
              .COULD_NOT_FIND_ANIMAL_FOR_ID,
            Long.toString(id)
          );

          return ServerResponse.status(HttpStatus.NOT_FOUND)
            .body(
              ex.toBody()
            );
        }

        return ServerResponse.ok()
          .body(
            Map.of("status", "ok")
          );
      }).DELETE("/animal/delete", req -> {
        Authentication session = SecurityContextHolder.getContext()
          .getAuthentication();
        
        if (Objects.isNull(session)) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .USER_UNAUTHORIZED
          );
          
          return ServerResponse.status(HttpStatus.UNAUTHORIZED)
            .body(
              ex.toBody()
            );
        }
        
        String username = (String)session.getPrincipal();
        
        Map data = req.body(Map.class);
        
        if (Objects.isNull(data))
          data = Map.of();
        
        Number idParameter = (Number)data.get("id");

        if (
          Objects.isNull(idParameter)
        ) {
          ServiceException ex = new ServiceException(
            ServiceException.Type
              .REQUIRED_PARAMETERS_MISSING,
            "id"
          );

          return ServerResponse.badRequest()
            .body(
              ex.toBody()
            );
        }
        
        long id = idParameter.longValue();
        
        try {
          if (!userSvc.isAnimalOwned(username, id)) {
            ServiceException ex = new AnimalServiceException(
              AnimalServiceException.Type
                .ANIMAL_NOT_OWNED,
              "animal " + id + " is not owned by user: " + username
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }
            
          userSvc.unassignAnimal(username, id);
          animalSvc.removeAnimal(id);
        } catch (AnimalService.NoAnimalFoundException ogEx) {
          ServiceException ex = new AnimalServiceException(
            AnimalServiceException.Type
              .COULD_NOT_FIND_ANIMAL_FOR_ID,
            Long.toString(id)
          );

          return ServerResponse.status(HttpStatus.NOT_FOUND)
            .body(
              ex.toBody()
            );
        }

        return ServerResponse.ok()
          .body(
            Map.of("status", "ok")
          );
      }).build();
}
    
}
