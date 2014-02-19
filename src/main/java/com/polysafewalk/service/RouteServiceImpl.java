package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polysafewalk.model.Route;

@Service
public class RouteServiceImpl implements RouteService {

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	@Override
	public List<Route> getRoutes(long fromArea, long toArea) {
		String sql = "SELECT * from route where from_area=? AND to_area=?";
		Connection conn = null;
		PreparedStatement ps = null;
		List<Route> routes = new ArrayList<Route>();

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, fromArea);
			ps.setLong(2, toArea);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Route route = new Route();
				route.setId(rs.getLong("id"));
				route.setTime(new Date(rs.getTime("time").getTime()));
				route.setNumber(countWalkers(route.getId()));
				routes.add(route);
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
		return routes;
	}

	@Override
	public int countWalkers(long route) {
		String sql = "SELECT count(*) as count from log where route_id = ? AND date=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			DateTime dt = new DateTime(DateTimeZone.forID("America/Los_Angeles"));
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
			String curDate = fmt.print(dt);
			
			ps.setLong(1, route);
			ps.setString(2, curDate);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt("count");
			} else {
				return 0;
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
		return 0;
	}

	@Override
	public Route getRoute(long routeId) {
		String sql = "SELECT * from route where id=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, routeId);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				Route route = new Route();
				route.setId(rs.getLong("id"));
				route.setTime(new Date(rs.getTime("time").getTime()));
				route.setNumber(countWalkers(route.getId()));
				return route;
			} else {
				return null;
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
		return null;
	}

}
