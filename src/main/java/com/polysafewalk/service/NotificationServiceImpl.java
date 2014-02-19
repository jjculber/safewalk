package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polysafewalk.model.Notification;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	@Override
	public void scheduleNotification(long userId, long routeId, DateTime time) {

		String sql = "INSERT INTO notification (user_id, route_id, time_to_send) VALUES (?,?,?)";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, userId);
			ps.setLong(2, routeId);

			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			String timeToSend = fmt.print(time);

			ps.setString(3, timeToSend);

			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return;
	}

	@Override
	public List<Notification> getNotifications() {
		String sql = "SELECT * from notification where time_to_send<? AND sent=0";
		Connection conn = null;
		PreparedStatement ps = null;

		List<Notification> notifications = new ArrayList<Notification>();

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			DateTime dt = new DateTime(DateTimeZone.forID("America/Los_Angeles"));
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			String timeToSend = fmt.print(dt);

			ps.setString(1, timeToSend);

			System.out.println(timeToSend);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Notification notification = new Notification();
				notification.setId(rs.getLong("id"));
				notification.setUserId(rs.getLong("user_id"));
				notification.setRouteId(rs.getLong("route_id"));
				notifications.add(notification);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return notifications;
	}

	@Override
	public void markNotificationSent(Notification notification, int value) {
		String sql = "UPDATE notification SET sent=? where id=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setInt(1, value);
			ps.setLong(2, notification.getId());
			ps.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
