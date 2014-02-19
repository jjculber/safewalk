package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polysafewalk.model.Log;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	@Override
	public void createLog(long userId, long routeId) {
		// TODO switch to date parameter
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
		// TODO switch to date parameter
		String sql = "SELECT fa.name as faname, ta.name as taname, "
				+ "r.time as rtime, l.id as lid, r.id as rid FROM log as l JOIN route as r on "
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
				log.setTime(new Date(rs.getTime("rtime").getTime()));
				log.setId(rs.getLong("lid"));
				log.setRouteId(rs.getLong("rid"));
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
