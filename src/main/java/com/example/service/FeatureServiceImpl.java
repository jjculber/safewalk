package com.example.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

@Service
public class FeatureServiceImpl implements FeatureService {
   
   private static final String cookieName = "features";

   @Autowired
   private DataSource dataSource;

   public void setDataSource(DataSource source) {
      dataSource = source;
   }

   @Override
   public boolean isEnabled(String name, HttpServletRequest request) {
      if (isEnabledFromCookie(name, request)) {
         return true;
      }
      return isEnabledFromDatabase(name);
   }
   
   private boolean isEnabledFromCookie(String name, HttpServletRequest request) {
      Cookie cookie = WebUtils.getCookie(request, cookieName);
      if (cookie == null) {
         return false;
      }
      String values = cookie.getValue();
      String[] features = values.split(",");
      for (String feature : features) {
         if (name.equals(feature)) {
            return true;
         }
      }
      return false;
   }
   
   private boolean isEnabledFromDatabase(String name) {
      String sql = "select enabled from live_feature_switch where name=?";
      Connection conn = null;
      PreparedStatement ps = null;

      try {
         conn = dataSource.getConnection();
         ps = conn.prepareCall(sql);

         ps.setString(1, name);
         ResultSet result = ps.executeQuery();

         while (result.next()) {
            return result.getBoolean(1);
         }
         return false;
      } catch (SQLException e) {
         // TODO
         e.printStackTrace();
      } finally {
         try {
            ps.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
      return false;
   }
   
}
