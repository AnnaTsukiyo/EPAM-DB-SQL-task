package com.epam.rd.java.basic.task7.db;

import com.epam.rd.java.basic.task7.db.entity.Team;
import com.epam.rd.java.basic.task7.db.entity.User;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBManager {

    private static final String INSERT_USER = "INSERT INTO users (login) VALUES (?)";
    private static final String INSERT_TEAM = "INSERT INTO teams (name) VALUES (?)";
    private static final String INSERT_TEAMS_FOR_USER = "INSERT INTO users_teams (user_id, team_id) VALUES (?, ?)";
    private static final String GET_USER = "SELECT * FROM users WHERE login = ?";
    private static final String GET_TEAM = "SELECT * FROM teams WHERE name = ?";
    private static final String FIND_ALL_USER = "SELECT * FROM users";
    private static final String FIND_ALL_TEAM = "SELECT * FROM teams";
    private static final String FIND_TEAM_BY_ID = "SELECT * FROM teams WHERE id = ?";
    private static final String FIND_USER_TEAMS = "SELECT * FROM users_teams WHERE user_id = ?";
    private static final String DELETE_TEAM = "DELETE FROM teams WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_TEAM = "UPDATE teams SET name = ? WHERE id = ?";

    private static DBManager instance;

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    public List<User> findAllUsers() throws DBException {
        List<User> userList = new ArrayList<>();
        String url = getUrl();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_USER);
        ) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                userList.add(mapUser(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return userList;
    }

    public List<Team> findAllTeams() throws DBException {
        List<Team> teamList = new ArrayList<>();
        String url = getUrl();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_TEAM);
        ) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                teamList.add(mapTeam(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return teamList;
    }

    public boolean insertUser(User user) throws DBException {
        String url = getUrl();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, user.getLogin());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return true;
    }

    public boolean insertTeam(Team team) throws DBException {
        String url = getUrl();
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement statement = connection.prepareStatement(INSERT_TEAM, Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, team.getName());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No rows affected");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    team.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return true;
    }

    public boolean deleteUsers(User... users) throws DBException {
        for (User user : users) {
            if (user == null) return false;
            try (Connection con = DriverManager.getConnection(getUrl());
                 PreparedStatement stmt = con.prepareStatement(DELETE_USER);
            ) {
                stmt.setInt(1, user.getId());
                stmt.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public boolean deleteTeam(Team team) throws DBException {
        if (team == null) return false;
        try (Connection con = DriverManager.getConnection(getUrl());
             PreparedStatement stmt = con.prepareStatement(DELETE_TEAM);
        ) {
            stmt.setInt(1, team.getId());
            stmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public User getUser(String login) throws DBException {
        User user = User.createUser(login);
        try (Connection con = DriverManager.getConnection(getUrl());
             PreparedStatement stmt = con.prepareStatement(GET_USER);
        ) {
            stmt.setString(1, login);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt("id"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return user;
    }

    public Team getTeam(String name) throws DBException {
        Team team = Team.createTeam(name);
        try (Connection con = DriverManager.getConnection(getUrl());
             PreparedStatement stmt = con.prepareStatement(GET_TEAM);
        ) {
            stmt.setString(1, name);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                team.setId(resultSet.getInt("id"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return team;
    }

    public boolean setTeamsForUser(User user, Team... teams) throws DBException {
        if (user == null) throw new DBException("", new NullPointerException());
        String url = getUrl();
        try (Connection connection = DriverManager.getConnection(url);
        ) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT_TEAMS_FOR_USER)) {
                for (Team team : teams) {
                    if (team == null) {
                        connection.rollback();
                        throw new DBException("", new NullPointerException());
                    }
                    statement.setString(1, String.valueOf(user.getId()));
                    statement.setString(2, String.valueOf(team.getId()));
                    statement.executeUpdate();
                }
            } catch (SQLException throwables) {
                connection.rollback();
                throw new DBException("", throwables);
            }
            connection.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            throw new DBException("", throwables);
        }

        return true;
    }

    public List<Team> getUserTeams(User user) throws DBException {
        List<Team> teamList = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(getUrl());
             PreparedStatement statement = connection.prepareStatement(FIND_USER_TEAMS);
        ) {
            statement.setInt(1, user.getId());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                teamList.add(mapTeamByTeamId(rs));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return teamList;
    }

    public boolean updateTeam(Team team) throws DBException {
        try (Connection con = DriverManager.getConnection(getUrl());
             PreparedStatement statement = con.prepareStatement(UPDATE_TEAM);
        ) {
            statement.setString(1, team.getName());
            statement.setInt(2, team.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    private String getUrl() {
        String url = null;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("app.properties"));
            url = properties.getProperty("connection.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = User.createUser(rs.getString("login"));
        user.setId(rs.getInt("id"));
        return user;
    }

    private Team mapTeam(ResultSet rs) throws SQLException {
        Team team = Team.createTeam(rs.getString("name"));
        team.setId(rs.getInt("id"));
        return team;
    }

    private Team mapTeamByTeamId(ResultSet rs) throws SQLException {
        try (Connection connection = DriverManager.getConnection(getUrl());
             PreparedStatement statement = connection.prepareStatement(FIND_TEAM_BY_ID);
        ) {
            statement.setInt(1, rs.getInt("team_id"));
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return mapTeam(resultSet);
        }
    }
}
