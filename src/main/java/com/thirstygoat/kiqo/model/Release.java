package com.thirstygoat.kiqo.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

/**
 * Created by leroy on 10/04/15.
 */
public class Release extends Item {
    private final StringProperty shortName;
    private final StringProperty description;
    private final ObjectProperty<Project> project;
    private final ObjectProperty<LocalDate> date;

    public Release(String shortName, Project project, LocalDate date, String description) {
        this.shortName = new SimpleStringProperty(shortName);
        this.project = new SimpleObjectProperty<>(project);
        this.description = new SimpleStringProperty(description);
        this.date = new SimpleObjectProperty<>(date);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Release release = (Release) o;

        return getShortName().equals(release.getShortName());
    }

    @Override
    public int hashCode() {
        return getShortName().hashCode();
    }
}
