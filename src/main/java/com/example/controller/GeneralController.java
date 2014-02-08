package com.example.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.service.FeatureService;

@Controller
public class GeneralController {

   @Autowired
   private FeatureService features;

   @RequestMapping("/")
   public String home(
            @RequestParam(value = "error", required = false) String error,
            Map<String, Object> map, HttpServletRequest request,
            HttpServletResponse response) {

      map.put("title", "Example Webapp");
      map.put("error", error);
      map.put("signupEnabled", features.isEnabled("signup", request));

      return "home";
   }

}
