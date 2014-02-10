package com.polysafewalk.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.TextEscapeUtils;

import com.polysafewalk.service.UserService;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

   @Autowired
   private UserService userService;
   
   @SuppressWarnings("deprecation")
   @Override
   public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {
      // TODO Auto-generated method stub
      if (!request.getMethod().equals("POST")) {
         throw new AuthenticationServiceException(
                  "Authentication method not supported: " + request.getMethod());
      }

      String username = obtainUsername(request);
      String password = obtainPassword(request);

      if (username == null) {
         username = "";
      }

      if (password == null) {
         password = "";
      }

      username = username.trim();

      UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
               username, password);

      // Place the last username attempted into HttpSession for views
      HttpSession session = request.getSession(false);

      if (session != null || getAllowSessionCreation()) {
         request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY,
                  TextEscapeUtils.escapeEntities(username));
      }

      // Allow subclasses to set the "details" property
      setDetails(request, authRequest);
      
      return userService.authenticateUser(authRequest);
   }
}
