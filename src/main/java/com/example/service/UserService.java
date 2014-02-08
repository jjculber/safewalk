package com.example.service;

import org.springframework.security.core.Authentication;

import com.example.exception.UsernameNotUniqueException;
import com.example.form.UserRegistrationForm;
import com.example.model.User;

public interface UserService {

   public User getUser(String username);

   public void createNewUser(UserRegistrationForm userRegistrationFormBean)
            throws UsernameNotUniqueException;
   
   Authentication authenticateUser(Authentication authToken);

}
