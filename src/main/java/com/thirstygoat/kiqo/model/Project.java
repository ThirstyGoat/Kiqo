package com.thirstygoat.kiqo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by samschofield on 22/04/15.
 */
public class Project extends Item {
    private final StringProperty shortName;
    private final StringProperty longName;
    private final ObservableList<Release> releases;
    private final ObservableList<Allocation> allocations;
    private final StringProperty description;

    /**
     * Dont use this if you are a person
     */
    public Project() {
        releases = FXCollections.observableArrayList();
        allocations = FXCollections.observableArrayList();
        shortName = new SimpleStringProperty();
        longName = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    /**
     * Create new Project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     */
    public Project(final String shortName, final String longName) {
        this();
        setShortName(shortName);
        setLongName(longName);
    }

    /**
     * Create a new project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param description description of the project
     */
    public Project(final String shortName, final String longName, final String description) {
        this();
        setShortName(shortName);
        setLongName(longName);
        setDescription(description);
    }

    /**
     *
     * @return Description of the project
     */
    public String getDescription() {
        return description.get();
    }

    /**
     *
     * @param description Description of the project
     */
    public void setDescription(final String description) {
        this.description.set(description);
    }

    /**
     *
     * @return longName Long name of the project
     */
    public String getLongName() {
        return longName.get();
    }

    /**
     *
     * @param longName Long name for the project
     */
    public void setLongName(final String longName) {
        this.longName.set(longName);
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    /**
     *
     * @param shortName Short name of the project
     */
    public void setShortName(final String shortName) {
        this.shortName.set(shortName);
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    /**
     * Gets the array of team allocations
     * @return The ObservableList of Team Allocations
     */
    public List<Allocation> getAllocations() {
        ArrayList<Allocation> allocations = new ArrayList<>();
        allocations.addAll(this.allocations);
        return allocations;
    }

    public ObservableList<Release> observableReleases() {
        return releases;
    }

    public List<Release> getReleases() {
        ArrayList<Release> releases = new ArrayList<>();
        releases.addAll(this.releases);
        return releases;
    }

    /**
     * @param releases list of releases associated with this project
     */
    public void setReleases(final List<Release> releases) {
        releases.clear();
        releases.addAll(releases);
    }

    @Override
    public String toString() {
        return "Project{shortName=" + shortName + ", longName=" + longName + ", description=" + description + ", releases=" + releases
                + ", allocations=" + allocations + "}";
    }

    public ObservableList<Allocation> observableAllocations() {
        return allocations;
    }
}
