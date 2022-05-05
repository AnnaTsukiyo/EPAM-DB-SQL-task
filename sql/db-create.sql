DROP database IF EXISTS testdb;

CREATE database test2db;

USE test2db;

CREATE TABLE users (
	id INT PRIMARY KEY auto_increment,
	login VARCHAR(10) UNIQUE
);

CREATE TABLE teams (
	id INT PRIMARY KEY auto_increment,
	name VARCHAR(10)
);

CREATE TABLE users_teams (
	user_id INT REFERENCES users(id) on delete cascade,
	team_id INT REFERENCES teams(id) on delete cascade,
	UNIQUE (user_id, team_id)
);

INSERT INTO users VALUES (DEFAULT, 'ivanov');
INSERT INTO teams VALUES (DEFAULT, 'teamA');

SELECT * FROM users;
SELECT * FROM teams;
SELECT * FROM users_teams;

