package com.example.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.example.exception.UsernameNotUniqueException;
import com.example.form.UserRegistrationForm;
import com.example.model.User;
import com.example.service.UserService;

@Controller
public class UsersController {

   @Autowired
   private UserService userService;

   @RequestMapping(value = "/signup.do", method = RequestMethod.POST)
   public String registerUser(UserRegistrationForm userRegistrationForm,
            HttpServletRequest request) {

      try {
         userService.createNewUser(userRegistrationForm);
         String username = userRegistrationForm.getUsername();
         String password = userRegistrationForm.getPassword();
         doAutoLogin(username, password, request);
         return "redirect:/chat.do";

      } catch (UsernameNotUniqueException e) {
         return "redirect:/?error=Username already exists";
      }
   }

   private void doAutoLogin(String username, String password,
            HttpServletRequest request) {
      try {
         // Must be called from request filtered by Spring Security, otherwise
         // SecurityContextHolder is not updated
         UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                  username, password);
         token.setDetails(new WebAuthenticationDetails(request));
         Authentication authentication = userService.authenticateUser(token);
         SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
         SecurityContextHolder.getContext().setAuthentication(null);
         e.printStackTrace();
      }
   }

   @RequestMapping("/profile.do")
   @Secured("ROLE_USER")
   public String userPage(Map<String, Object> map) {
      User user = (User) SecurityContextHolder.getContext().getAuthentication()
               .getPrincipal();

      map.put("username", user.getUsername());
      map.put("profile", "active");
      map.put("title", "&lt; Profile &gt;");

      return "user";
   }
}
