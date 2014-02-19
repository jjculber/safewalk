package com.polysafewalk.service;

import java.util.Date;
import java.util.List;

import com.polysafewalk.model.Notification;

public interface NotificationService {

	public void scheduleNotification(long userId, long routeId, Date time);

	public List<Notification> getNotifications();

	public void markNotificationSent(Notification notification, int value);

}
