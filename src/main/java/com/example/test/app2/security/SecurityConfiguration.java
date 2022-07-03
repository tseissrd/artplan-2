package com.example.test.app2.security;

import com.example.test.app2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 * @author tseissrd
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
  
  @Autowired
  private UserService userSvc;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http)
  throws Exception {
    return http
      .headers().frameOptions().disable()
      .and()
      .csrf().disable()
      .authorizeHttpRequests(requests -> requests
        .antMatchers(
          "/user/login",
          "/user/register",
          "/user/checkName",
          "/h2",
          "/h2/*"
        ).permitAll()
        .anyRequest()
        .authenticated()
      ).httpBasic(withDefaults())
      .authenticationManager(userSvc)
      .build();
  }
}