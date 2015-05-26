package com.thirstygoat.kiqo.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.util.Utilities;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Organisation implements Serializable {
    private final ObservableList<Project> projects;
    private final ObservableList<Person> people;
    private final ObservableList<Skill> skills;
    private final ObservableList<Team> teams;
    private final StringProperty organisationName;

    private final Skill poSkill;
    private final Skill smSkill;

    private transient File saveLocation;

    public Organisation() {
        projects = FXCollections.observableArrayList(Item.getWatchStrategy());
        people = FXCollections.observableArrayList(Item.getWatchStrategy());
        skills = FXCollections.observableArrayList(Item.getWatchStrategy());
        teams = FXCollections.observableArrayList(Item.getWatchStrategy());
        organisationName = new SimpleStringProperty("Untitled");
        poSkill = new Skill("PO", "Product Owner");
        smSkill = new Skill("SM", "Scrum Master");
        skills.add(poSkill);
        skills.add(smSkill);
    }

    public Organisation(final File saveLocation) {
        this();
        this.saveLocation = saveLocation;
    }

    public Skill getPoSkill() {
        return poSkill;
    }

    public Skill getSmSkill() {
        return smSkill;
    }

    /**
     *
     * @return ObservableList of people in project
     */
    public ObservableList<Person> getPeople() {
        return people;
    }

    /**
     *
     * @return A list of people with the Product Owner (PO) skill
     */
    public List<Person> getEligiblePOs() {
        List<Person> eligiblePOs = new ArrayList<>();
        for (Person person : people) {
            if (person.getSkills().contains(poSkill)) {
                eligiblePOs.add(person);
            }
        }
        return eligiblePOs;
    }

    /**
     *
     * @return ObservableList of skills in project
     */
    public ObservableList<Skill> getSkills() {
        return skills;
    }

    /**
     *
     * @return save location of project
     */
    public File getSaveLocation() {
        return saveLocation;
    }

    /**
     *
     * @param saveLocation Save location of project
     */
    public void setSaveLocation(final File saveLocation) {
        this.saveLocation = saveLocation;
        organisationName.set(Utilities.stripExtension(saveLocation.getName()));
    }

    public StringProperty organisationNameProperty() {
        return organisationName;
    }

    /**
     *
     * @return ObservableList of teams
     */
    public ObservableList<Team> getTeams() {
        return teams;
    }

    /**
     *
     * @return Observable list of projects
     */
    public ObservableList<Project> getProjects() {
        return projects;
    }
}
