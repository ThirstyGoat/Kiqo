package com.thirstygoat.kiqo.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

import javafx.beans.property.*;
import javafx.collections.*;

import com.thirstygoat.kiqo.search.SearchableField;
import com.thirstygoat.kiqo.util.Utilities;


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
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchStrings = new ArrayList<>();
        searchStrings.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()), new SearchableField("Description", getDescription()),
                new SearchableField("Release Date", getDate().format(Utilities.DATE_TIME_FORMATTER))));
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
    
    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(shortName);
        bps.addPropertyChangeSupportFor(description);
        bps.addPropertyChangeSupportFor(project);
        bps.addPropertyChangeSupportFor(date);
    }
}
