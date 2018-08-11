package com.skilldistillery.filmquery.app;

import java.sql.SQLException;
import java.util.ArrayList;
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
	

	public static void main(String[] args) {
		FilmQueryApp app = new FilmQueryApp();
//    	app.test();
		app.launch();
	}

	private void test()  {
		try {
			film = db.getFilmById(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(film);
		try {
			actor = db.getActorById(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(actor);
	}

	private void launch() {
		Scanner sc = new Scanner(System.in);

		startUserInterface(sc);
		sc.close();
	}

	private void startUserInterface(Scanner sc) {
		boolean flag = true;

		System.out.println("Welcome to The Film Archive!");

		while (flag) {
			List<Film> films = new ArrayList<>();
			try {
				printMenu();
				String input = sc.nextLine();
				int userInput = Integer.parseInt(input);

				switch (userInput) {
				case 1:
					searchFilmId(sc);
					sc.nextLine();
					break;
				case 2:
					films = searchByKeyword(sc);
					displayFilms(sc, films);
					sc.nextLine();
					break;
				case 0:
					System.out.println("Goodbye.");
					flag = false;
					break;
				}

			} catch (NumberFormatException e) {
				System.out.println("Invalid input.");
			}
		}
	}
	
	public void printMenu() {
		System.out.println("What would you like to do?");
		System.out.println("1. Search for a film by the Film ID");
		System.out.println("2. Search for a film by Title or a keyword");
		System.out.println("0. Exit the application");
		System.out.print("> ");
	}
	
	public void searchFilmId(Scanner sc) {
		System.out.print("Please enter the Film ID: ");
		int filmId = sc.nextInt();

		try {
			if (db.getFilmById(filmId) != null) {
				System.out.println(db.getFilmById(filmId));
			}
			else {
				System.out.println("Film not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<Film> searchByKeyword(Scanner sc) {
		List<Film> films = new ArrayList<>();
		System.out.print("Please enter a Title or keyword to search: ");
		String keyword = sc.nextLine();
		try {
			films = db.getFilmsByKeyword(keyword);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return films;
	}

	public void displayFilms(Scanner sc, List<Film> films) {
		int userInput;
		if (!(films.isEmpty())) {
			for (int i = 0; i < films.size(); i++) {
				System.out.println("Film ID: " + films.get(i).getFilmId() + ", Title: " +films.get(i).getTitle());
			}
			System.out.println("\nWould you like to view the details for a specifc film? (1) ");
			System.out.println("Would you like to view the details for each film? (2) ");
			System.out.print("Or return to the main menu? (3) \n> ");
			userInput = sc.nextInt();
			if (userInput == 1) {
				searchFilmId(sc);
			}
			else if (userInput == 2) {
				for (int i = 0; i < films.size(); i++) {
					System.out.println(films.get(i));
					System.out.println("***************************");
					System.out.println();
				} 
			}
			films.clear();
		} else {
			System.out.println("Film not found.");
		}
	}
}
