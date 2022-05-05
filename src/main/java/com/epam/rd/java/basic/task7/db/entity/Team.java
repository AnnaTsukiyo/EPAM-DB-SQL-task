package com.epam.rd.java.basic.task7.db.entity;

import java.util.Objects;

public class Team {

    private String name;
    private int id;

    private Team(String name) {
        this.id = 0;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Team team = (Team) o;
        return Objects.equals(name, team.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public static Team createTeam(String name) {
        return new Team(name);
    }

    @Override
    public String toString() {
        return name;
    }

}
