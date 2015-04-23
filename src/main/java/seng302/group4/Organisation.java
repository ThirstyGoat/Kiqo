package seng302.group4;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
    private final ArrayList<Release> releases = new ArrayList<>();
    private final Skill poSkill = new Skill("PO", "Product Owner");
    private final Skill smSkill = new Skill("SM", "Scrum Master");
    private transient ObservableList<Project> projectsObservable = FXCollections.observableList(projects);
    private transient ObservableList<Person> peopleObservable = FXCollections.observableList(people);
    private transient ObservableList<Skill> skillsObservable = FXCollections.observableList(skills);
    private transient ObservableList<Team> teamsObservable = FXCollections.observableList(teams);
    // even though releases are stored under Project, organisation has to keep track of them
    private transient ObservableList<Release> releaseObservable = FXCollections.observableArrayList(releases);
    private transient File saveLocation;

    public Organisation() {
        skills.add(poSkill);
        skills.add(smSkill);

        // register listener on projects list to listen for changes in releases
        // derived from http://docs.oracle.com/javafx/2/api/javafx/collections/ListChangeListener.Change.html
        projectsObservable.addListener(new ListChangeListener<Project>() {
            @Override
            public void onChanged(javafx.collections.ListChangeListener.Change<? extends Project> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        // ignore permutate
                    } else if (c.wasUpdated() || c.wasReplaced()) {
                        replaceReleaseList(c.getList());
                    } else {
                        for (final Project remitem : c.getRemoved()) {
                            releaseObservable.removeAll(remitem.getReleases());
                        }
                        for (final Project additem : c.getAddedSubList()) {
                            releaseObservable.addAll(additem.getReleases());
                        }
                    }
                }
            }

            private void replaceReleaseList(ObservableList<? extends Project> list) {
                releaseObservable.clear();
                for (final Project project : list) {
                    releaseObservable.addAll(project.getReleases());
                }
            }
        });
    }

    public Organisation(final File saveLocation) {
        this();
        this.saveLocation = saveLocation;
    }

    public static void main(String[] args) {
        final Organisation o = new Organisation(new File("/Users/samschofield/Desktop/org.json"));
        o.getPeople().add(new Person("sam", null, null, null, null, null, null, null));
        try {
            PersistenceManager.saveOrganisation(new File("/Users/samschofield/Desktop/org.json"), o);
        } catch (final IOException e) {
            e.printStackTrace();
        }

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
    public ObservableList<Release> getReleases() {
        return releaseObservable;
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
        releaseObservable = FXCollections.observableList(releases);

        // Set the observable lists for each of the projects
        getProjects().forEach(seng302.group4.Project::setObservableLists);
    }
}
