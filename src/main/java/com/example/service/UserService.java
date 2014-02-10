package com.example.service;

import org.springframework.security.core.Authentication;

import com.example.exception.UsernameNotUniqueException;
import com.example.form.UserRegistrationForm;

public interface UserService {

	public void createNewUser(UserRegistrationForm userRegistrationFormBean,
			String confirmKey) throws UsernameNotUniqueException;

	Authentication authenticateUser(Authentication authToken);
	
	public void confirmKey(String confirmKey);

}
