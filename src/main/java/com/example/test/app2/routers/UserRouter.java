package com.example.test.app2.routers;

import com.example.test.app2.routers.error.ServiceException;
import com.example.test.app2.routers.error.UserServiceException;
import com.example.test.app2.service.UserService;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import static org.springframework.web.servlet.function.RequestPredicates.*;
import static org.springframework.web.servlet.function.RouterFunctions.*;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

/**
 *
 * @author tseissrd
 */
@Configuration
public class UserRouter {
  
  @Autowired
  private UserService userService;
    
  @Bean
  public RouterFunction<ServerResponse> getUserRouter() {
    return route()
      
      .GET(
        "/user/checkName",
        accept(APPLICATION_JSON),
        req -> {
          Map data = req.body(Map.class);
          
          if (Objects.isNull(data))
            data = Map.of();

          if (!data.containsKey("username")) {
            ServiceException ex = new ServiceException(
              ServiceException.Type
                .REQUIRED_PARAMETERS_MISSING,
              "username"
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }

          String username = (String)data.get("username");

          return ServerResponse.ok()
            .contentType(APPLICATION_JSON)
            .body(
              Map.of(
                "status", "ok",
                "exists", userService.checkUsername(username)
              )
            );
          
        }).GET("/user/login", req -> {
          Map data = req.body(Map.class);
          
          if (Objects.isNull(data))
            data = Map.of();

          if (
            !data.containsKey("username")
            || !data.containsKey("password")
          ) {
            ServiceException ex = new ServiceException(
              ServiceException.Type
                .REQUIRED_PARAMETERS_MISSING,
              "username, password"
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }
              
          String username = (String)data.get("username");
          String password = (String)data.get("password");

          try {
            Authentication session = (Authentication)userService.authenticate(username, password);
            
            return ServerResponse.ok()
              .body(
                Map.of(
                  "status", "ok",
                  "token", session.getCredentials()
                )
              );
          } catch (AuthenticationException ogEx) {
            ServiceException ex = new UserServiceException(
              UserServiceException.Type
                .AUTHENTICATION_FAILURE,
              ogEx.getLocalizedMessage()
            );

            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
              .body(
                ex.toBody()
              );
          }
          
        }).POST("/user/register", req -> {
          Map data = req.body(Map.class);
          
          if (Objects.isNull(data))
            data = Map.of();

          if (
            !data.containsKey("username")
            || !data.containsKey("password")
          ) {
            ServiceException ex = new ServiceException(
              ServiceException.Type
                .REQUIRED_PARAMETERS_MISSING,
              "username, password"
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }

          String username = (String)data.get("username");
          String password = (String)data.get("password");

          try {
            userService.register(username, password);
          } catch (UserService.UserExistsException ogEx) {
            ServiceException ex = new UserServiceException(
              UserServiceException.Type
                .USER_EXISTS,
              ogEx.getLocalizedMessage()
            );

            return ServerResponse.badRequest()
              .body(
                ex.toBody()
              );
          }

          Authentication session = (Authentication)userService.authenticate(username, password);
          return ServerResponse.ok()
            .body(
              Map.of(
                "status", "ok",
                "token", session.getCredentials()
              )
            );
        }).build();
}
    
}
