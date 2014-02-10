package com.polysafewalk.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.stereotype.Service;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.polysafewalk.exception.UsernameNotUniqueException;
import com.polysafewalk.form.UserRegistrationForm;
import com.polysafewalk.model.User;

@SuppressWarnings("deprecation")
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private DataSource dataSource;

	public void setDataSource(DataSource source) {
		dataSource = source;
	}

	@Override
	public void createNewUser(UserRegistrationForm form, String confirmKey)
			throws UsernameNotUniqueException {

		String sql = "INSERT INTO users (first_name, last_name, email, password, confirmation) values (?,?,?,SHA2(?,512),?)";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			ps.setString(1, form.getFirstName());
			ps.setString(2, form.getLastName());
			ps.setString(3, form.getEmail().toLowerCase());
			ps.setString(4, form.getEmail().toLowerCase() + form.getPassword());
			ps.setString(5, confirmKey);

			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			throw new UsernameNotUniqueException();
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

	@Override
	public Authentication authenticateUser(Authentication authToken) {
		String sql = "SELECT * from users WHERE email=? AND password=SHA2(?,512)";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			String email = ((String) authToken.getPrincipal()).toLowerCase();
			String password = email + (String) authToken.getCredentials();

			ps.setString(1, email);
			ps.setString(2, password);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				User user = new User();
				user.setEmail(rs.getString("email"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setConfirmKey(rs.getString("confirmation"));
				user.setId(rs.getLong("id"));

				UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
						user, authToken.getCredentials(),
						Collections.singleton(new GrantedAuthorityImpl(
								"ROLE_USER")));
				return token;
			} else {
				throw new BadCredentialsException("bad credentials.");
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
		throw new BadCredentialsException("bad credentials.");
	}

	@Override
	public User getUserById(long id) {
		String sql = "SELECT * from users WHERE id=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareCall(sql);

			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				User user = new User();
				user.setEmail(rs.getString("email"));
				user.setFirstName(rs.getString("first_name"));
				user.setLastName(rs.getString("last_name"));
				user.setConfirmKey(rs.getString("confirmation"));
				user.setId(rs.getLong("id"));

				return user;
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
	public void confirmKey(String confirmKey) {

		String sql = "UPDATE users SET confirmation=NULL where confirmation=?";
		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = dataSource.getConnection();
			ps = conn.prepareStatement(sql);

			ps.setString(1, confirmKey);
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
