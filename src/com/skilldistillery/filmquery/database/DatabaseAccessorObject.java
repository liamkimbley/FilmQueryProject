package com.skilldistillery.filmquery.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class DatabaseAccessorObject implements DatabaseAccessor {
	private Film film = null;
	private Actor actor = null;
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private List<Film> films = new ArrayList<>();
	private static final String url = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";
	private static final String user = "student";
	private static final String pass = "student";
	private static final String getFilm = "SELECT id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features FROM film WHERE film.id = ?";
	private static final String getActor = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
	private static final String actorsByFilm = "SELECT actor.id, actor.first_name, actor.last_name FROM actor JOIN film_actor ON film_actor.actor_id = actor.id JOIN film on film.id = film_actor.film_id WHERE film_id = ?";
	private static final String getFilmsByActor = "SELECT id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features FROM film JOIN film_actor ON film.id = film_actor.film_id WHERE actor_id = ?";
	private static final String getFilmsByKeyword = "SELECT id, title, description, release_year, language_id, rental_duration, rental_rate, length, replacement_cost, rating, special_features FROM film WHERE (title LIKE ? OR description LIKE ?)";
	private static final String getLanguage = "SELECT name FROM language JOIN film ON language.id = film.language_id WHERE film.language_id = ?";
	private static final String getCondition = "SELECT i.media_condition FROM inventory_item i JOIN film f ON f.id = i.film_id WHERE f.id = ?";


	@Override
	public Film getFilmById(int filmId) throws SQLException {
		ResultSet rs = null;

		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(getFilm);
			stmt.setInt(1, filmId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				film = addFilm(rs);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return film;
	}

	@Override
	public Actor getActorById(int actorId) throws SQLException {
		ResultSet rs = null;

		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(getActor);
			stmt.setInt(1, actorId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				actor = new Actor(); // Create the object
				// Here is our mapping of query columns to our object fields:
				actor.setId(rs.getInt(1));
				actor.setFirstName(rs.getString(2));
				actor.setLastName(rs.getString(3));
				actor.setFilms(getFilmsByActorId(actorId)); // An Actor has Films
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return actor;
	}

	@Override
	public List<Actor> getActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();
		ResultSet rs = null;

		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(actorsByFilm);
			stmt.setInt(1, filmId);
			rs = stmt.executeQuery();

			while (rs.next()) {
				int actorId = rs.getInt(1);
				String firstName = rs.getString(2);
				String lastName = rs.getString(3);
				Actor actor = new Actor(actorId, firstName, lastName);
				actors.add(actor);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return actors;
	}
	
	public List<Film> getConditionByFilmId(int filmId) {
		List<Film> films = new ArrayList<>();
		ResultSet rs = null;
		String condition = null;
		
		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(getCondition);
			stmt.setInt(1, filmId);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				condition = rs.getString(1);
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return films;
	}
	
	public List<Film> getFilmsByActorId(int actorId) throws SQLException {
		ResultSet rs = null;

		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(getFilmsByActor);
			stmt.setInt(1, actorId);
			rs = stmt.executeQuery();
			while (rs.next()) {
				film = addFilm(rs);
				films.add(film);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
		return films;
	}

	public List<Film> getFilmsByKeyword(String key) throws SQLException {
				ResultSet rs = null;

		try {
			conn = getDBConnection();
			stmt = conn.prepareStatement(getFilmsByKeyword);
			stmt.setString(1, "%" + key + "%");
			stmt.setString(2, "%" + key + "%");
			rs = stmt.executeQuery();
			while (rs.next()) {
				film = addFilm(rs);
				films.add(film);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}

		return films;
	}

	public String getLang(Connection conn, int langId) {
		String result = null;
		ResultSet rs = null;

		try {
			stmt = conn.prepareStatement(getLanguage);
			stmt.setInt(1, langId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Film addFilm(ResultSet rs) {
		String language = null;
		int lang = 0;

		try {
			lang = rs.getInt(5);
			language = getLang(conn, lang);
			film = new Film(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getShort(4), language, rs.getInt(6),
					rs.getDouble(7), rs.getInt(8), rs.getDouble(9), rs.getString(10), rs.getString(11));
			
			film.setActorsList(getActorsByFilmId(film.getFilmId()));

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return film;
	}
	
		private Connection getDBConnection() {
		
		try {
			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
			conn = DriverManager.getConnection(url, user, pass);
			return conn;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return conn;
	}

}
