package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polysafewalk.model.Area;
import com.polysafewalk.model.Log;
import com.polysafewalk.model.Route;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	List<Area> fromAreas;
	List<Area> toAreas;

	@Override
	public List<Area> getFromAreas() {
		if (fromAreas != null) {
			return fromAreas;
		}
		String sql = "SELECT * from from_area";
		Connection conn = null;
		PreparedStatement ps = null;
		List<Area> areas = new ArrayList<Area>();

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Area area = new Area();
				area.setName(rs.getString("name"));
				area.setDescription(rs.getString("description"));
				area.setId(rs.getLong("id"));
				areas.add(area);
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
		fromAreas = areas;
		return fromAreas;
	}

	@Override
	public List<Area> getToAreas() {
		if (toAreas != null) {
			return toAreas;
		}
		String sql = "SELECT * from to_area";
		Connection conn = null;
		PreparedStatement ps = null;
		List<Area> areas = new ArrayList<Area>();

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Area area = new Area();
				area.setName(rs.getString("name"));
				area.setDescription(rs.getString("description"));
				area.setId(rs.getLong("id"));
				areas.add(area);
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
		toAreas = areas;
		return toAreas;
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
		String sql = "SELECT count(*) as count from log where date=curdate() AND route_id = ?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, route);

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
	public void createLog(long userId, long routeId) {
		String sql = "INSERT INTO log (user_id, route_id, date) VALUES (?,?,CURDATE())";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, userId);
			ps.setLong(2, routeId);

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
	public Log getLog(long userId) {
		String sql = "SELECT fa.name as faname, ta.name as taname, "
				+ "r.time as rtime, l.id as lid FROM log as l JOIN route as r on "
				+ "l.route_id = r.id JOIN from_area as fa on r.from_area = "
				+ "fa.id JOIN to_area as ta on r.to_area = ta.id "
				+ "WHERE l.user_id=? AND l.date=CURDATE()";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, userId);

			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				Log log = new Log();
				log.setFromAreaName(rs.getString("faname"));
				log.setToAreaName(rs.getString("taname"));
				// TODO: format
				log.setTime(new Date(rs.getTime("rtime").getTime()));
				log.setId(rs.getLong("lid"));
				return log;
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

	@Override
	public void deleteLog(Log log) {
		String sql = "UPDATE log SET date='00-00-0000' WHERE id=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, log.getId());

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

}
