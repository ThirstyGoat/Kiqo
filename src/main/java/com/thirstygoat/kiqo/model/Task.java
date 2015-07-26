package com.thirstygoat.kiqo.model;

import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.util.Callback;

/**
 * Created by samschofield on 23/07/15.
 */
public class Task extends Item {
    private final StringProperty shortName;
    private final StringProperty description;
    private final FloatProperty estimate;
    private final ObjectProperty<Status> status;

    public Task() {
        shortName = new SimpleStringProperty("");
        description = new SimpleStringProperty("");
        estimate = new SimpleFloatProperty(0.0f);
        status = new SimpleObjectProperty<>(Status.NOT_STARTED);
    }

    public Task(String shortName, String description, Float estimate) {
        this.shortName = new SimpleStringProperty(shortName);
        this.description = new SimpleStringProperty(description);
        this.estimate = new SimpleFloatProperty(estimate);
        status = new SimpleObjectProperty<>(Status.NOT_STARTED);
    }

    public ObjectProperty<Status> statusProperty() {
        return status;
    }

    public Status getStatus() {
        return status.get();
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

    public static Callback<Task, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty(), p.estimateProperty()};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (!shortName.equals(task.shortName)) return false;
        if (description != null ? !description.equals(task.description) : task.description != null) return false;
        if (!estimate.equals(task.estimate)) return false;
        return !(status != null ? !status.equals(task.status) : task.status != null);

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
