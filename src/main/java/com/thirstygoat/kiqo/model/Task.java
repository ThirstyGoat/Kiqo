package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.SearchableField;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by samschofield on 23/07/15.
 */
public class Task extends Item {
    private final StringProperty shortName;
    private final StringProperty description;
    private final FloatProperty estimate;
    private final ObjectProperty<Status> status;
    private final ObjectProperty<Story> story;
    private final ObservableList<Impediment> impediments;

    public Task() {
        shortName = new SimpleStringProperty("");
        description = new SimpleStringProperty("");
        estimate = new SimpleFloatProperty(0.0f);
        status = new SimpleObjectProperty<>(Status.NOT_STARTED);
        story = new SimpleObjectProperty<>(null);
        impediments = FXCollections.observableArrayList(Impediment.getWatchStrategy());
    }

    public Task(String shortName, String description, Float estimate, Story story) {
        this();
        setShortName(shortName);
        setDescription(description);
        setEstimate(estimate);
        setStatus(Status.NOT_STARTED);
        setStory(story);
    }

    public static Callback<Task, Observable[]> getWatchStrategy() {
        return p -> new Observable[]{p.shortNameProperty(), p.estimateProperty(), p.statusProperty()};
    }

    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(shortName);
        bps.addPropertyChangeSupportFor(description);
        bps.addPropertyChangeSupportFor(estimate);
        bps.addPropertyChangeSupportFor(status);
        bps.addPropertyChangeSupportFor(story);
    }

    /**
     * @return a string array of the searchable fields for a model object
     */
    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchableFields = new ArrayList<>();
        searchableFields.add(new SearchableField("Short Name", getShortName()));
        searchableFields.add(new SearchableField("Description", getDescription()));

        return searchableFields;
    }

    public ObservableList<Impediment> getImpediments() {
        return impediments;
    }

    public Story getStory() {
        return story.get();
    }

    public void setStory(Story story) {
        this.story.set(story);
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public Status getStatus() {
        return status.get();
    }

    public void setStatus(Status status) {
        this.status.set(status);
    }

    public FloatProperty estimateProperty() {
        return estimate;
    }

    public float getEstimate() {
        return estimate.get();
    }

    public void setEstimate(float estimate) {
        estimateProperty().setValue(estimate);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        descriptionProperty().setValue(description);
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    public void setShortName(String shortName) {
        shortNameProperty().setValue(shortName);
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (!shortName.get().equals(task.shortName.get())) return false;
        if (description.get() != null ? !description.get().equals(task.description.get()) : task.description.get() != null)
            return false;
        if (estimate.get() != (task.estimate.get())) return false;
        return !(status.get() != null ? !status.get().equals(task.status.get()) : task.status.get() != null);

    }

    @Override
    public int hashCode() {
        int result = shortName.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + estimate.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
