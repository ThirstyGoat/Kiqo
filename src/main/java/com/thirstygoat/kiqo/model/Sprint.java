package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created by Bradley on 31/07/15.
 */
public class Sprint extends Item {
    private ObjectProperty<Backlog> backlog;
    private ObjectProperty<LocalDate> startDate;
    private ObjectProperty<LocalDate> endDate;
    private ObjectProperty<Team> team;
    private ObjectProperty<Release> release;
    private StringProperty goal;
    private StringProperty longName;
    private StringProperty description;
    private ObservableList<Story> stories;

    public Sprint() {
        backlog = new SimpleObjectProperty<>(null);
        startDate = new SimpleObjectProperty<>(null);
        endDate = new SimpleObjectProperty<>(null);
        team = new SimpleObjectProperty<>(null);
        release = new SimpleObjectProperty<>(null);
        goal = new SimpleStringProperty("");
        longName = new SimpleStringProperty("");
        description = new SimpleStringProperty("");
        stories = FXCollections.observableArrayList(Story.getWatchStrategy());
    }

    public Sprint(String goal, String longName, String description, Backlog backlog, Release release,
                  Team team, LocalDate startDate, LocalDate endDate, Collection<Story> stories) {
        this();
        setGoal(goal);
        setLongName(longName);
        setDescription(description);
        setBacklog(backlog);
        setRelease(release);
        setTeam(team);
        setStartDate(startDate);
        setEndDate(endDate);
        getStories().addAll(stories);
    }

    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(backlog);
        bps.addPropertyChangeSupportFor(startDate);
        bps.addPropertyChangeSupportFor(endDate);
        bps.addPropertyChangeSupportFor(team);
        bps.addPropertyChangeSupportFor(release);
        bps.addPropertyChangeSupportFor(goal);
        bps.addPropertyChangeSupportFor(longName);
        bps.addPropertyChangeSupportFor(description);
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<SearchableField> searchStrings = new ArrayList<>();
        searchStrings.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()), new SearchableField("Description", getDescription()),
                new SearchableField("Long Name", getLongName()),
                new SearchableField("Start Date", getStartDate().format(dateTimeFormatter)), new SearchableField("End Date", getEndDate().format(dateTimeFormatter))));
        return searchStrings;
    }


    @Override
    public StringProperty shortNameProperty() {
        return goal;
    }

    public Backlog getBacklog() {
        return backlog.get();
    }

    public void setBacklog(Backlog backlog) {
        this.backlog.set(backlog);
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return backlog;
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

    public String getGoal() {
        return goal.get();
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
