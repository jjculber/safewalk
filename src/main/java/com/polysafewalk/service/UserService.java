package com.polysafewalk.service;

import org.springframework.security.core.Authentication;

import com.polysafewalk.exception.UsernameNotUniqueException;
import com.polysafewalk.form.UserRegistrationForm;
import com.polysafewalk.model.User;

public interface UserService {

	public void createNewUser(UserRegistrationForm userRegistrationFormBean,
			String confirmKey) throws UsernameNotUniqueException;

	Authentication authenticateUser(Authentication authToken);
	
	public void confirmKey(String confirmKey);
	
	public User getUserById(long id);

}
