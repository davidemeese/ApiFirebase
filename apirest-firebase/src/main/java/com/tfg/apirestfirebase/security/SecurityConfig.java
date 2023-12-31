package com.tfg.apirestfirebase.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.tfg.apirestfirebase.security.token.FirebaseEntryPoint;
import com.tfg.apirestfirebase.security.token.FirebaseFilter;
import com.tfg.apirestfirebase.security.token.FirebaseProvider;

@Configuration
public class SecurityConfig {

	@Autowired
	FirebaseEntryPoint entryPoint;

	@Autowired
	FirebaseProvider provider;

	@Bean 
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { 
		http.csrf().disable().authorizeRequests().anyRequest().authenticated();
		http.exceptionHandling().authenticationEntryPoint(entryPoint);
		http.addFilterBefore(new FirebaseFilter(), BasicAuthenticationFilter.class);
		http.authenticationProvider(provider);
		return http.build(); 
	}
	
}
