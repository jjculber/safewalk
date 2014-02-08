package com.example.service;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.stereotype.Service;

import com.example.exception.UsernameNotUniqueException;
import com.example.form.UserRegistrationForm;
import com.example.model.User;
import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;

@SuppressWarnings("deprecation")
@Service
public class UserServiceImpl implements UserService {

   @Autowired
   private DataSource dataSource;

   public void setDataSource(DataSource source) {
      dataSource = source;
   }

   @Override
   public User getUser(String username) {
      return null;
   }

   @Override
   public void createNewUser(UserRegistrationForm userRegistrationForm)
            throws UsernameNotUniqueException {
      // TODO add validation on username

      String sql = "{call create_account(?,?,?)}";
      Connection conn = null;
      CallableStatement cs = null;

      try {
         conn = dataSource.getConnection();
         cs = conn.prepareCall(sql);

         cs.setString(1, userRegistrationForm.getUsername());
         cs.setString(2, userRegistrationForm.getPassword());
         cs.registerOutParameter(3, Types.BIGINT);
         cs.executeQuery();
      } catch (MySQLIntegrityConstraintViolationException e) {
         throw new UsernameNotUniqueException();
      } catch (SQLException e) {
         // TODO
         e.printStackTrace();
      } finally {
         try {
            cs.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public Authentication authenticateUser(Authentication authToken) {
      String sql = "{call authenticate_account(?,?,?,?,?,?)}";
      Connection conn = null;
      CallableStatement cs = null;

      try {
         conn = dataSource.getConnection();
         cs = conn.prepareCall(sql);

         cs.setString(1, (String) authToken.getPrincipal());
         cs.setString(2, (String) authToken.getCredentials());
         cs.setString(3, "website_frontend");
         cs.setString(4, "web");
         cs.registerOutParameter(5, Types.VARCHAR);
         cs.registerOutParameter(6, Types.BIGINT);
         cs.executeQuery();

         String handle = cs.getString(5);
         long channelId = cs.getLong(6);
         if (handle == null) {
            throw new BadCredentialsException("bad credentials.");
         }

         User user = new User();
         user.setUsername(handle);
         user.setChannelId(channelId);

         UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                  user, authToken.getCredentials(),
                  Collections.singleton(new GrantedAuthorityImpl("ROLE_USER")));
         return token;
      } catch (SQLException e) {
         e.printStackTrace();
      } finally {
         try {
            cs.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return null;
   }

}
