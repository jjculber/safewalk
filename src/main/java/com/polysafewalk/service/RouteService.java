package com.polysafewalk.service;

import java.util.List;

import com.polysafewalk.model.Route;

public interface RouteService {

	public List<Route> getRoutes(long fromArea, long toArea);
	public Route getRoute(long routeId);
	public int countWalkers(long route);

}
