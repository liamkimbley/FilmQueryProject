package com.skilldistillery.filmquery.database;

import java.sql.SQLException;
import java.util.List;

import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public interface DatabaseAccessor {
	public Film getFilmById(int filmId) throws SQLException;
	public Actor getActorById(int actorId) throws SQLException;
	public List<Actor> getActorsByFilmId(int filmId);
	public List<Film> getFilmsByKeyword(String key) throws SQLException;

}
