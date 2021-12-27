package com.epam.rd.java.basic.task7.db;

import java.util.List;

import com.epam.rd.java.basic.task7.db.entity.*;


public class DBManager {

	private static DBManager instance;

	public static synchronized DBManager getInstance() {
		return null;
	}

	private DBManager() {
	}

	public List<User> findAllUsers() throws DBException {
		return null;
	}

	public boolean insertUser(User user) throws DBException {
		return false;
	}

	public boolean deleteUsers(User... users) throws DBException {
		return false;
	}

	public User getUser(String login) throws DBException {
		return null;
	}

	public Team getTeam(String name) throws DBException {
		return null;
	}

	public List<Team> findAllTeams() throws DBException {
		return null;
	}

	public boolean insertTeam(Team team) throws DBException {
		return false;
	}

	public boolean setTeamsForUser(User user, Team... teams) throws DBException {
		return false;	
	}

	public List<Team> getUserTeams(User user) throws DBException {
		return null;	
	}

	public boolean deleteTeam(Team team) throws DBException {
		return false;
	}

	public boolean updateTeam(Team team) throws DBException {
		return false;
	}

}
