package com.tfg.apirestfirebase.security.model;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.google.firebase.auth.UserRecord;

public class UserModel implements Authentication{
	
	private UserRecord userRecord;

	public UserModel(UserRecord userRecord) {
		this.userRecord = userRecord;
	}

	@Override
	public String getName() {
		return userRecord.getDisplayName();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getCredentials() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDetails() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAuthenticated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}
