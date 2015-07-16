package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 20/05/15.
 */
public class Backlog extends Item {
    private StringProperty shortName;
    private StringProperty longName;
    private StringProperty description;
    private ObjectProperty<Person> productOwner;
    private ObjectProperty<Project> project;
    private final ObservableList<Story> stories = FXCollections.observableArrayList();

    public Backlog() {
        this.shortName = new SimpleStringProperty("");
        this.longName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.productOwner = new SimpleObjectProperty<>(null);
        this.project = new SimpleObjectProperty<>(null);
    }

    public Backlog(String shortName, String longName, String description, Person productOwner,
                   Project project, List<Story> stories) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.productOwner = new SimpleObjectProperty<>(productOwner);
        this.project = new SimpleObjectProperty<>(project);
        this.stories.addAll(stories);
    }

    public List<Story> getStories() {
        List<Story> stories1 = new ArrayList<>();
        stories1.addAll(stories);
        return stories1;
    }

    public ObservableList<Story> observableStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories.clear();
        this.stories.addAll(stories);
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getProductOwner() {
        return productOwner.get();
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return productOwner;
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner.set(productOwner);
    }

    public Project getProject() {
        return project.get();
    }

    public ObjectProperty<Project> projectPropert() {
        return project;
    }

    public void setProject(Project project) {
        this.project.set(project);
    }
}
