package com.example.service;

import java.util.List;

import com.example.model.Area;
import com.example.model.Log;
import com.example.model.Route;

public interface AreaService {

	public List<Area> getFromAreas();
	public List<Area> getToAreas();
	public List<Route> getRoutes(long fromArea, long toArea);
	public void createLog(long userId, long routeId);
	public Log getLog(long userId);
	public void deleteLog(Log log);
	public int countWalkers(long route);

}
