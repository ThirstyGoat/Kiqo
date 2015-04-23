package seng302.group4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Organisation {
    private final ArrayList<Project> projects = new ArrayList<>();
    private final ArrayList<Skill> skills = new ArrayList<>();
    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Person> people = new ArrayList<>();
    private transient ObservableList<Project> projectsObservable;
    private transient ObservableList<Person> peopleObservable;
    private transient ObservableList<Skill> skillsObservable;
    private transient ObservableList<Team> teamsObservable;

    private transient File saveLocation;

    private final Skill poSkill = new Skill("PO", "Product Owner");
    private final Skill smSkill = new Skill("SM", "Scrum Master");

    public Organisation() {
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
        return peopleObservable;
    }

    /**
     *
     * @return ObservableList of skills in project
     */
    public ObservableList<Skill> getSkills() {
        return skillsObservable;
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
    }

    /**
     *
     * @return ObservableList of teams
     */
    public ObservableList<Team> getTeams() {
        return teamsObservable;
    }

    /**
     *
     * @return Observable list of projects
     */
    public ObservableList<Project> getProjects() {
        return projectsObservable;
    }

    public void setObservableLists() {
        projectsObservable = FXCollections.observableList(projects);
        peopleObservable = FXCollections.observableList(people);
        skillsObservable = FXCollections.observableList(skills);
        teamsObservable = FXCollections.observableList(teams);
    }
}
