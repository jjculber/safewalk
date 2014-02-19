package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.polysafewalk.model.Area;

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

}
