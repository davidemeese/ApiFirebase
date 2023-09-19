package com.tfg.apirestfirebase.security.model;

import javax.security.auth.Subject;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class TokenModel extends AbstractAuthenticationToken {
	
	private String token;
	
	public TokenModel(String token) {
		super(null);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean implies(Subject subject) {
		// TODO Auto-generated method stub
		return super.implies(subject);
	}

}
