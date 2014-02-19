package com.polysafewalk.service;

import java.util.List;

import org.joda.time.DateTime;

import com.polysafewalk.model.Notification;

public interface NotificationService {

	public void scheduleNotification(long userId, long routeId, DateTime time);

	public List<Notification> getNotifications();

	public void markNotificationSent(Notification notification, int value);

}
