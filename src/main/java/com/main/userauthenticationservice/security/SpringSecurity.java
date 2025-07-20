package com.main.userauthenticationservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// Special class -> Can define multiple objects within this class(@Bean)
@Configuration
public class SpringSecurity {
    /* By default, Spring security start authenticating all the apis as soon we add in pom
      SecurityFilterChain Object Handles which object should be authenticated and which aren't
   */
    @Bean
    public SecurityFilterChain filteringCriterion(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        http.authorizeHttpRequests(auth->auth.anyRequest().permitAll());
        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
