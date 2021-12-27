package com.epam.rd.java.basic.task7;

import java.util.List;

import com.epam.rd.java.basic.task7.db.*;
import com.epam.rd.java.basic.task7.db.entity.*;

public class Demo {

	private static void print(List<?> list) {
		list.forEach(System.out::println);
	}

	public static void main(String[] args) throws DBException {
		// users  ==> [ivanov]
		// teams  ==> [teamA]

		DBManager dbManager = DBManager.getInstance();

		// Part 1
		dbManager.insertUser(User.createUser("petrov"));
		dbManager.insertUser(User.createUser("obama"));

		dbManager.findAllUsers().forEach(System.out::println);
		print(dbManager.findAllUsers());
		// users  ==> [ivanov, petrov, obama]

		System.out.println("===========================");

		// Part 2
		dbManager.insertTeam(Team.createTeam("teamB"));
		dbManager.insertTeam(Team.createTeam("teamC"));

		print(dbManager.findAllTeams());
		// teams ==> [teamA, teamB, teamC]        

		System.out.println("===========================");

		// Part 3
		User userPetrov = dbManager.getUser("petrov");
		User userIvanov = dbManager.getUser("ivanov");
		User userObama = dbManager.getUser("obama");

		Team teamA = dbManager.getTeam("teamA");
		Team teamB = dbManager.getTeam("teamB");
		Team teamC = dbManager.getTeam("teamC");

		// method setTeamsForUser must implement transaction!
		dbManager.setTeamsForUser(userIvanov, teamA);
		dbManager.setTeamsForUser(userPetrov, teamA, teamB);
		dbManager.setTeamsForUser(userObama, teamA, teamB, teamC);

		for (User user : dbManager.findAllUsers()) {
			print(dbManager.getUserTeams(user));
			System.out.println("~~~~~");
		}
		// teamA
		// teamA teamB
		// teamA teamB teamC

		// Part 4
		// on delete cascade!
		dbManager.deleteTeam(teamA);

		// Part 5
		teamC.setName("teamX");
		dbManager.updateTeam(teamC);
		print(dbManager.findAllTeams());
		// teams ==> [teamB, teamX]

		System.out.println("===========================");
		
		// Part 6
		dbManager.deleteUsers(dbManager.findAllUsers().toArray(User[]::new));
		for (Team team : dbManager.findAllTeams()) {
			dbManager.deleteTeam(team);
		}

		dbManager.insertUser(User.createUser("ivanov"));
		dbManager.insertTeam(Team.createTeam("teamA"));
		
		print(dbManager.findAllTeams());
		// teams ==> [teamA]        

		print(dbManager.findAllUsers());
		// users ==> [ivanov]        
	}

}
