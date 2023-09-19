package com.tfg.apirestfirebase.config;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseConfig {

	private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);
	
	@Value("${firebase.config.path}")
	private String configPath;
	
	@PostConstruct
	public void init() throws IOException {
		ClassPathResource resource = new ClassPathResource(configPath);
		
		FirebaseOptions options = FirebaseOptions.builder()
			    .setCredentials(GoogleCredentials.fromStream(resource.getInputStream()))
			    .setDatabaseUrl("https://tfg-login-8ea38-default-rtdb.europe-west1.firebasedatabase.app")
			    .build();

			FirebaseApp.initializeApp(options);
			logger.info("App name: {}", FirebaseApp.getInstance().getName());
	}
}
