package seng302.group4;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;


/**
 * Created by samschofield on 22/04/15.
 */
public class Project extends Item {
    private final ArrayList<Release> releases = new ArrayList<>();
    private final ArrayList<Allocation> allocations = new ArrayList<>();
    private String shortName;
    private String longName;
    private String description;
    /* We interact with the observableLists, but serialise the arrayLists */
    private transient ObservableList<Release> releasesObservable = FXCollections.observableList(releases);
    private transient ObservableList<Allocation> allocationsObservable = FXCollections.observableList(allocations);

    /**
     * Create new Project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     */
    public Project(final String shortName, final String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    /**
     * Create a new project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param description description of the project
     */
    public Project(final String shortName, final String longName, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
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

    @Override
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
     * Gets the array of team allocations
     * @return The ObservableList of Team Allocations
     */
    public ObservableList<Allocation> getAllocations() {
        return allocationsObservable;
    }


//    /**
//     * Gets the array of all allocations belonging to team
//     * @param team The team to get allocations for
//     * @return An ArrayList of the teams allocations
//     */
//    public ArrayList<Allocation> getTeamsAllocations(Team team) {
//        ArrayList<Allocation> teamsAllocations = new ArrayList<>();
//        for (Allocation allocation : teamAllocations) {
//            if (allocation.getTeam().getShortName().equals(team.getShortName())) {
//                teamsAllocations.add(allocation);
//            }
//        }
//        return teamsAllocations;
//    }

    public ObservableList<Release> getReleases() {
        return releasesObservable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Project project = (Project) o;

        return shortName.equals(project.shortName);

    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    @Override
    public String toString() {
        return "Project{" +
                "shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public void setObservableLists() {
        releasesObservable = FXCollections.observableList(releases);
        allocationsObservable = FXCollections.observableList(allocationsObservable);
    }
}
