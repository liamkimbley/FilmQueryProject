package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.Scanner;

import com.skilldistillery.filmquery.database.DatabaseAccessor;
import com.skilldistillery.filmquery.database.DatabaseAccessorObject;
import com.skilldistillery.filmquery.entities.Actor;
import com.skilldistillery.filmquery.entities.Film;

public class FilmQueryApp {

	private DatabaseAccessor db = new DatabaseAccessorObject();
	private Film film;
	private Actor actor;

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//    	app.test();
		app.launch();
	}

	private void test() {
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
			System.out.println("What would you like to do?");
			System.out.println("1. Search for a film by the Film ID");
			System.out.println("2. Search for a film by a keyword");
			System.out.println("0. Exit the application");
			System.out.print("> ");
			String input = sc.nextLine();
			int userInput = Integer.parseInt(input);

			switch (userInput) {
			case 1:
				System.out.print("Please enter the Film ID: ");
				int filmID = sc.nextInt();

				if (db.getFilmById(filmID) != null) {
					System.out.println(db.getFilmById(filmID).getTitle());
				} else {
					System.out.println("Film not found.");
				}
				break;
			case 2:
				System.out.print("Please enter a keyword to search: ");
				String keyword = sc.nextLine();
				try {
					System.out.println(db.getFilmsByKeyword(keyword).toString());
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case 0:
				System.out.println("Goodbye.");
				System.exit(1);
				break;
			}

		} catch (NumberFormatException e) {
			System.out.println("Invalid input.");
			sc.nextLine();
		} finally {

		}

	}

}
