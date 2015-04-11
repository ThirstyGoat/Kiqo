package seng302.group4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Project class represents a software project
 *
 * Generic getter/setter from http://stackoverflow.com/a/28673716
 */
public class Project implements Serializable {
    private final ArrayList<Person> people = new ArrayList<>();
    private final ArrayList<Skill> skills = new ArrayList<>();
    private final ArrayList<Team> teams = new ArrayList<>();
    private final ArrayList<Release> releases = new ArrayList<>();
    private transient ObservableList<Person> peopleObservable = FXCollections.observableList(people);
    private transient ObservableList<Skill> skillsObservable = FXCollections.observableList(skills);
    private transient ObservableList<Team> teamsObservable = FXCollections.observableList(teams);
    private transient ObservableList<Release> releaseObservable = FXCollections.observableList(releases);

    private String shortName;
    private String longName;
    private String description;
    private transient File saveLocation;

    private Skill poSkill = new Skill("PO", "Product Owner");
    private Skill smSkill = new Skill("SM", "Scrum Master");

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Project() {
        skills.add(poSkill);
        skills.add(smSkill);
    }

    /**
     * Create new Project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param saveLocation save location for the project
     */
    public Project(final String shortName, final String longName, final File saveLocation) {
        this();
        this.shortName = shortName;
        this.longName = longName;
        this.saveLocation = saveLocation;
    }

    /**
     * Create a new project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param saveLocation save location for the project
     * @param description description of the project
     */
    public Project(final String shortName, final String longName, final File saveLocation, final String description) {
        this();
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
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
     * @return ObservableList of releases in project
     */
    public ObservableList<Release> getRelease() {
        return releaseObservable;
    }

    /**
     *
     * @param release Release to add to the list of releases in Project
     */
    public void addRelease(final Release release) {
        releaseObservable.add(release);
    }

    /**
     *
     * @param release Release to remove from the list of releases in Project
     */
    public void removeRelease(final Release release) {
        releaseObservable.remove(release);
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final Project project = (Project) o;

        if (!shortName.equals(project.shortName)) {
            return false;
        }

        return true;
    }

    /**
     *
     * @return Description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description Description of the project
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return longName Long name of the project
     */
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @param longName Long name for the project
     */
    public void setLongName(final String longName) {
        this.longName = longName;
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
     * @return Short name of project
     */
    public String getShortName() {
        return shortName;
    }

    /**
     *
     * @param shortName Short name of the project
     */
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    /**
     *
     * @return ObservableList of teams
     */
    public ObservableList<Team> getTeams() {
        return teamsObservable;
    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    public void prepareForDestruction() {
        // FIXME Auto-generated method stub
        // eg. remove people
    }

    @Override
    public String toString() {
        return "Project{" + "shortName='" + shortName + '\'' + ", longName='" + longName + '\'' + ", description='" + description + '\''
                + ", saveLocation='" + saveLocation + '\'' + '}';
    }

    public void removePerson(final Person person) {
        peopleObservable.remove(person);
    }

    public void setObservableLists() {
        peopleObservable = FXCollections.observableList(people);
        skillsObservable = FXCollections.observableList(skills);
        teamsObservable = FXCollections.observableList(teams);
    }

}
