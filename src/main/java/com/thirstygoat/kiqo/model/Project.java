package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by samschofield on 22/04/15.
 */
public class Project extends Item {
    private final StringProperty shortName;
    private final StringProperty longName;
    private final ObservableList<Release> releases;
    private final ObservableList<Story> unallocatedStories;
    private final ObservableList<Allocation> allocations;
    private final ObservableList<Backlog> backlogs;
    private final ObservableList<Sprint> sprints;
    private final StringProperty description;

    /**
     * Dont use this if you are a person
     */
    public Project() {
        releases = FXCollections.observableArrayList(Item.getWatchStrategy());
        unallocatedStories = FXCollections.observableArrayList(Item.getWatchStrategy());
        backlogs = FXCollections.observableArrayList();
        allocations = FXCollections.observableArrayList();
        sprints = FXCollections.observableArrayList();
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
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchStrings = new ArrayList<>();
        searchStrings.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()), new SearchableField("Long Name", getLongName()),
                new SearchableField("Description", getDescription())));
        return searchStrings;
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
        final ArrayList<Allocation> allocations = new ArrayList<>();
        allocations.addAll(this.allocations);
        return allocations;
    }

    /**
     *
     * @return List of allocations which are current
     */
    public List<Allocation> getCurrentAllocations()  {
        final LocalDate now = LocalDate.now();
        final ArrayList<Allocation> currentAllocations = new ArrayList<>();
        for (Allocation allocation : getAllocations()) {
            if (allocation.isCurrent()) {
                currentAllocations.add(allocation);
            }
        }
        return currentAllocations;
    }

    public ObservableList<Release> observableReleases() {
        return releases;
    }

    public ObservableList<Story> observableUnallocatedStories() {
        return unallocatedStories;
    }

    public ObservableList<Backlog> observableBacklogs() {
        return backlogs;
    }

    public ObservableList<Sprint> observableSprints() {
        return sprints;
    }

    public List<Release> getReleases() {
        final List<Release> releases = new ArrayList<>();
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

    /**
     * @return list of unallocatedStories associated with this project
     */
    public List<Story> getUnallocatedStories() {
        final List<Story> stories = new ArrayList<>();
        stories.addAll(this.unallocatedStories);
        return stories;
    }

    /**
     * @param unallocatedStories list of unallocatedStories associated with this project
     */
    public void setUnallocatedStories(final List<Story> unallocatedStories) {
        this.unallocatedStories.clear();
        this.unallocatedStories.addAll(unallocatedStories);
    }

    /**
     *
     * @return list of backlogs associated with this project.
     */
    public List<Backlog> getBacklogs() {
        final List<Backlog> backlogs = new ArrayList<>();
        backlogs.addAll(this.backlogs);
        return backlogs;
    }

    /**
     *
     * @param backlogs list of backlogs associated with this project
     */
    public void setBacklogs(final List<Backlog> backlogs) {
        this.backlogs.clear();
        this.backlogs.addAll(backlogs);
    }

    /**
     *
     * @return list of sprints associated with this project.
     */
    public List<Sprint> getSprints() {
        final List<Sprint> sprints = new ArrayList<>();
        sprints.addAll(this.sprints);
        return sprints;
    }

    /**
     *
     * @param sprints list of sprints associated with this project
     */
    public void setSprints(final List<Sprint> sprints) {
        this.sprints.clear();
        this.sprints.addAll(sprints);
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
