package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.search.SearchableField;

/**
 * Created by leroy on 20/05/15.
 */
public class Backlog extends Item {
    private final ObservableList<Story> stories = FXCollections.observableArrayList();
    private StringProperty shortName;
    private StringProperty longName;
    private StringProperty description;
    private ObjectProperty<Person> productOwner;
    private ObjectProperty<Project> project;
    private ObjectProperty <Scale> scale;

    public Backlog() {
        this.shortName = new SimpleStringProperty("");
        this.longName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.productOwner = new SimpleObjectProperty<>(null);
        this.project = new SimpleObjectProperty<>(null);
        this.scale = new SimpleObjectProperty<>(Scale.FIBONACCI);
    }

    public Backlog(String shortName, String longName, String description, Person productOwner,
                   Project project, List<Story> stories, Scale scale) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.productOwner = new SimpleObjectProperty<>(productOwner);
        this.project = new SimpleObjectProperty<>(project);
        this.scale = new SimpleObjectProperty<>(scale);
        this.stories.addAll(stories);
    }

    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(shortName);
        bps.addPropertyChangeSupportFor(longName);
        bps.addPropertyChangeSupportFor(description);
        bps.addPropertyChangeSupportFor(productOwner);
        bps.addPropertyChangeSupportFor(project);
        bps.addPropertyChangeSupportFor(scale);
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString =  new ArrayList<>();
        searchString.addAll(Arrays.asList(new SearchableField("Short Name", getShortName()),
                new SearchableField("Long Name", getLongName()), new SearchableField("Description", getDescription())));
        if (getProductOwner() != null) searchString.add(new SearchableField("PO", getProductOwner().getShortName()));
        return searchString;
    }

    public Scale getScale() {
        return scale.get();
    }

    public void setScale(Scale scale) {
        this.scale.set(scale);
    }

    public List<Story> getStories() {
        List<Story> stories1 = new ArrayList<>();
        stories1.addAll(stories);
        return stories1;
    }

    public void setStories(List<Story> stories) {
        this.stories.clear();
        this.stories.addAll(stories);
    }

    public ObservableList<Story> observableStories() {
        return stories;
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

    public Person getProductOwner() {
        return productOwner.get();
    }

    public void setProductOwner(Person productOwner) {
        this.productOwner.set(productOwner);
    }

    public ObjectProperty<Person> productOwnerProperty() {
        return productOwner;
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

    public ObjectProperty<Scale> scaleProperty() {
        return scale;
    }
}
