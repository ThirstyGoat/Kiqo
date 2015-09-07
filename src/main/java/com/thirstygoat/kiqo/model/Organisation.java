package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.SearchableItems;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Organisation implements Serializable {
    private final ObservableList<Project> projects;
    private final ObservableList<Person> people;
    private final ObservableList<Release> releases;
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
        releases = FXCollections.observableArrayList(Item.getWatchStrategy());
        teams = FXCollections.observableArrayList(Item.getWatchStrategy());
        organisationName = new SimpleStringProperty("Untitled");
        poSkill = null;
        smSkill = null;
    }

    public Organisation(boolean addDefaultItems) {
        projects = FXCollections.observableArrayList(Item.getWatchStrategy());
        people = FXCollections.observableArrayList(Item.getWatchStrategy());
        releases = FXCollections.observableArrayList(Item.getWatchStrategy());
        skills = FXCollections.observableArrayList(Item.getWatchStrategy());
        teams = FXCollections.observableArrayList(Item.getWatchStrategy());
        organisationName = new SimpleStringProperty("Untitled");
        if (addDefaultItems) {
            poSkill = new Skill("PO", "Product Owner");
            smSkill = new Skill("SM", "Scrum Master");
            skills.addAll(poSkill, smSkill);
            SearchableItems.getInstance().addSearchable(poSkill);
            SearchableItems.getInstance().addSearchable(smSkill);
        } else {
            poSkill = smSkill = null;
        }
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
     * @return ObservableList of releases
     */
    public ObservableList<Release> getReleases() {
        return releases;
    }

    /**
     *
     * @return ObservableList of skills in project
     */
    public ObservableList<Skill> getSkills() {
        return skills;
    }

    public List<Skill> getSkillsList() {
        // temp fix to allow undo add skill to person
        final ArrayList<Skill> skills1 = new ArrayList<>();
        skills1.addAll(skills);
        return skills1;
//        return Collections.unmodifiableList(skills);
    }

    public void setSkills(List<Skill> skills) {
        this.skills.clear();
        this.skills.addAll(skills);
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
