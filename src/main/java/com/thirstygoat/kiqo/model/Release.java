package com.thirstygoat.kiqo.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by leroy on 10/04/15.
 */
public class Release extends Item implements Serializable {
    private final StringProperty shortName;
    private final StringProperty description;
    private final ObjectProperty<Project> project;
    private final ObjectProperty<LocalDate> date;
    private final ObservableList<Sprint> sprints;

    public Release() {
        this.shortName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.project = new SimpleObjectProperty<>(null);
        this.date = new SimpleObjectProperty<>(null);
        this.sprints = FXCollections.observableArrayList();
    }


    public Release(String shortName, Project project, LocalDate date, String description) {
        this.shortName = new SimpleStringProperty(shortName);
        this.project = new SimpleObjectProperty<>(project);
        this.description = new SimpleStringProperty(description);
        this.date = new SimpleObjectProperty<>(date);
        this.sprints = FXCollections.observableArrayList();
    }

    public ObservableList<Sprint> getSprints() {
        return sprints;
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<String> getSearchableStrings() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<String> searchStrings = new ArrayList<>();
        searchStrings.addAll(Arrays.asList(getShortName(), getDescription(), getDate().format(dateTimeFormatter)));
        return searchStrings;
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public Project getProject() {
        return project.get();
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    @Override
    public String toString() {
        return "Release{shortName=" + shortName + ", project=" + project.get().getShortName() + ", date=" + date + ", description="
                + description + "}";
    }
}
