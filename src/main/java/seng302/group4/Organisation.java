package seng302.group4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seng302.group4.utils.Utilities;

import java.io.File;
import java.io.Serializable;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Organisation implements Serializable {
    private final Skill poSkill = new Skill("PO", "Product Owner");
    private final Skill smSkill = new Skill("SM", "Scrum Master");
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private ObservableList<Person> people = FXCollections.observableArrayList();
    private ObservableList<Skill> skills = FXCollections.observableArrayList();
    private ObservableList<Team> teams = FXCollections.observableArrayList();
    private transient File saveLocation;
    private String organisationName = "Untitled";


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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Organisation that = (Organisation) o;

        return !(saveLocation != null ? !saveLocation.equals(that.saveLocation) : that.saveLocation != null);

    }

    @Override
    public int hashCode() {
        return saveLocation != null ? saveLocation.hashCode() : 0;
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
        organisationName = Utilities.stripExtention(saveLocation.getName());
    }


    public String getOrganisationName() {
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
