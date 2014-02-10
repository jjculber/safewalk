package com.polysafewalk.service;

import java.util.List;

import com.polysafewalk.model.Area;
import com.polysafewalk.model.Log;
import com.polysafewalk.model.Route;

public interface AreaService {

	public List<Area> getFromAreas();
	public List<Area> getToAreas();
	public List<Route> getRoutes(long fromArea, long toArea);
	public void createLog(long userId, long routeId);
	public Log getLog(long userId);
	public void deleteLog(Log log);
	public int countWalkers(long route);

}
