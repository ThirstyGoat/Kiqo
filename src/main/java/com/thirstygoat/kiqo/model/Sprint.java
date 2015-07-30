package com.thirstygoat.kiqo.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Created by Bradley on 31/07/15.
 */
public class Sprint extends Item {

    private ObjectProperty<Project> project;
    private ObjectProperty<LocalDate> startDate;
    private ObjectProperty<LocalDate> endDate;
    private ObjectProperty<Team> team;
    private ObjectProperty<Release> release;
    private StringProperty goal;
    private StringProperty longName;
    private StringProperty description;
    private ObservableList<Story> stories;

    public Sprint() {
        project = new SimpleObjectProperty<>();
        startDate = new SimpleObjectProperty<>();
        endDate = new SimpleObjectProperty<>();
        team = new SimpleObjectProperty<>();
        release = new SimpleObjectProperty<>();
        goal = new SimpleStringProperty();
        longName = new SimpleStringProperty();
        description = new SimpleStringProperty();
        stories = FXCollections.observableArrayList(Item.getWatchStrategy());
    }

    public Sprint(String goal, String longName, String description, Project project, Release release,
                  Team team, LocalDate startDate, LocalDate endDate, Collection<Story> stories) {
        this();
        setGoal(goal);
        setLongName(longName);
        setDescription(description);
        setProject(project);
        setRelease(release);
        setTeam(team);
        setStartDate(startDate);
        setEndDate(endDate);
        getStories().addAll(stories);
    }

    @Override
    public StringProperty shortNameProperty() {
        return goal;
    }

    public Project getProject() {
        return project.get();
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public LocalDate getStartDate() {
        return startDate.get();
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate.set(startDate);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate.get();
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate.set(endDate);
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }

    public Team getTeam() {
        return team.get();
    }

    public void setTeam(Team team) {
        this.team.set(team);
    }

    public ObjectProperty<Team> teamProperty() {
        return team;
    }

    public Release getRelease() {
        return release.get();
    }

    public void setRelease(Release release) {
        this.release.set(release);
    }

    public ObjectProperty<Release> releaseProperty() {
        return release;
    }

    public void setGoal(String goal) {
        this.goal.set(goal);
    }

    public String getLongName() {
        return longName.get();
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public ObservableList<Story> getStories() {
        return stories;
    }

    public void setStories(ObservableList<Story> stories) {
        this.stories = stories;
    }
}
