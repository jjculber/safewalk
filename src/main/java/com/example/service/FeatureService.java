package com.example.service;

import javax.servlet.http.HttpServletRequest;

public interface FeatureService {

   public boolean isEnabled(String name, HttpServletRequest request);

}
