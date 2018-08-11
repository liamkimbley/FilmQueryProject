package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	private DatabaseAccessor db = new DatabaseAccessorObject();
	private Film film;
	private Actor actor;
	List<Film> films = null;
	

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//    	app.test();
		app.launch();
	}

	private void test() throws SQLException {
		film = db.getFilmById(1);
		System.out.println(film);
		actor = db.getActorById(1);
		System.out.println(actor);
	}

	private void launch() {
		Scanner sc = new Scanner(System.in);

		startUserInterface(sc);

		sc.close();
	}

	private void startUserInterface(Scanner sc) {

		System.out.println("Welcome to The Film Archive!");

		try {
			printMenu();
			String input = sc.nextLine();
			int userInput = Integer.parseInt(input);

			switch (userInput) {
			case 1:
				searchFilmId(sc);
				break;
			case 2:
				films = searchByKeyword(sc);
				if ( !(films.isEmpty())) {
					for (int i = 0; i < films.size(); i++) {
						System.out.println(films.get(i));
						System.out.println("***************************");
						System.out.println();
					} 
				}
				else {
					System.out.println("Film not found.");
				}
				break;
			case 0:
				System.out.println("Goodbye.");
				break;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid input.");
			sc.nextLine();
		} finally {

		}
	}
	
	
	public void printMenu() {
		
		System.out.println("What would you like to do?");
		System.out.println("1. Search for a film by the Film ID");
		System.out.println("2. Search for a film by a keyword");
		System.out.println("0. Exit the application");
		System.out.print("> ");
	}
	
	public void searchFilmId(Scanner sc) {
		System.out.print("Please enter the Film ID: ");
		int filmId = sc.nextInt();

		try {
			if (db.getFilmById(filmId) != null) {
				System.out.println(db.getFilmById(filmId).getTitle());
			}
			else {
				System.out.println("Film not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Film> searchByKeyword(Scanner sc) {
		System.out.print("Please enter a keyword to search: ");
		String keyword = sc.nextLine();
		try {
			films = db.getFilmsByKeyword(keyword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

}
