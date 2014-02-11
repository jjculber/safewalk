package com.polysafewalk.service;

import java.util.Date;
import java.util.List;

import com.polysafewalk.model.Area;
import com.polysafewalk.model.Log;
import com.polysafewalk.model.Notification;
import com.polysafewalk.model.Route;

public interface AreaService {

	public List<Area> getFromAreas();
	public List<Area> getToAreas();
	public List<Route> getRoutes(long fromArea, long toArea);
	public Route getRoute(long routeId);
	public void createLog(long userId, long routeId);
	public Log getLog(long userId);
	public void deleteLog(Log log);
	public int countWalkers(long route);
	public void scheduleNotification(long userId, long routeId, Date time);
	public List<Notification> getNotifications();
	public void markNotificationSent(Notification notification, long value);

}
