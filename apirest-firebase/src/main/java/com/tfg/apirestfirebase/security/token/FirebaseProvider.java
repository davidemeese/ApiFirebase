package com.tfg.apirestfirebase.security.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.tfg.apirestfirebase.security.model.TokenModel;
import com.tfg.apirestfirebase.security.model.UserModel;

@Component
public class FirebaseProvider implements AuthenticationProvider{

	private static final Logger logger = LoggerFactory.getLogger(FirebaseProvider.class);
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		TokenModel token = (TokenModel) authentication;
		try {
			FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(token.getToken(), true);
			String uid = firebaseToken.getUid();
			UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
			return new UserModel(userRecord);
		} catch (FirebaseAuthException e) {
			logger.error("Fail: {}", getErrorCode(e.getAuthErrorCode()));
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return TokenModel.class.isAssignableFrom(authentication);
	}
	
	private String getErrorCode(AuthErrorCode errorCode) {
		String error;
		switch (errorCode.toString()) {
			case "EXPIRED_ID_TOKE":
				error = "token expired";
				break;
			case "INVALID_ID_TOKE":
				error = "token invalid";
				break;
			case "REVOKED_ID_TOKE":
				error = "token revoked";
				break;
			default:
				error = "authentication fail";
				break;
		}
		return error;
	}

}
