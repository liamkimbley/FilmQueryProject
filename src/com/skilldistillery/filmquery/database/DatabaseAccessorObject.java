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
	private static final String url = "jdbc:mysql://localhost:3306/sdvid?useSSL=false";
	private String user = "student";
	private String pass = "student";

	@Override
	public Film getFilmById(int filmId) {
		Film film = null;
		String sql = "SELECT id, title FROM film WHERE id = ?";
		
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);
			ResultSet filmResult = stmt.executeQuery();
			if (filmResult.next()) {
				film = new Film();
				film.setFilmId(filmResult.getInt(1));
				film.setTitle(filmResult.getString(2));
				film.setDesc(filmResult.getString(3));
				film.setReleaseYear(filmResult.getShort(4));
				film.setLangId(filmResult.getInt(5));
				film.setRentDur(filmResult.getInt(6));
				film.setRate(filmResult.getDouble(7));
				film.setLength(filmResult.getInt(8));
				film.setRepCost(filmResult.getDouble(9));
				film.setRating(filmResult.getString(10));
				film.setFeatures(filmResult.getString(11));
			}
			
			filmResult.close();
			stmt.close();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return film;
	}
	
	@Override
	public Actor getActorById(int actorId) {
		Actor actor = null;

		String sql = "SELECT id, first_name, last_name FROM actor WHERE id = ?";
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
			ResultSet actorResult = stmt.executeQuery();
			if (actorResult.next()) {
				actor = new Actor(); // Create the object
				// Here is our mapping of query columns to our object fields:
				actor.setId(actorResult.getInt(1));
				actor.setFirstName(actorResult.getString(2));
				actor.setLastName(actorResult.getString(3));
				actor.setFilms(getFilmsByActorId(actorId)); // An Actor has Films
			}
				
				actorResult.close();
				stmt.close();
				conn.close();
				
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return actor;
	}
	
	@Override
	public List<Actor> getActorsByFilmId(int filmId) {
		List<Actor> actors = new ArrayList<>();
		
		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			String sql = "SELECT actor.id, actor.first_name, actor.last_name FROM actor JOIN film_actor ON film_actor.actor_id = actor.id JOIN film on film.id = film_actor.film_id WHERE film_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, filmId);
			ResultSet rs = stmt.executeQuery();
			
			while(rs.next()) {
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

	public List<Film> getFilmsByActorId(int actorId) {
		List<Film> films = new ArrayList<>();

		try {
			Connection conn = DriverManager.getConnection(url, user, pass);
			String sql = "SELECT id, title, description, release_year, language_id, rental_duration, ";
			sql += " rental_rate, length, replacement_cost, rating, special_features "
					+ " FROM film JOIN film_actor ON film.id = film_actor.film_id " + " WHERE actor_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, actorId);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int filmId = rs.getInt(1);
				String title = rs.getString(2);
				String desc = rs.getString(3);
				short releaseYear = rs.getShort(4);
				int langId = rs.getInt(5);
				int rentDur = rs.getInt(6);
				double rate = rs.getDouble(7);
				int length = rs.getInt(8);
				double repCost = rs.getDouble(9);
				String rating = rs.getString(10);
				String features = rs.getString(11);
				Film film = new Film(filmId, title, desc, releaseYear, langId, rentDur, rate, length, repCost, rating,
						features);
				films.add(film);
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}
}
