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

    public static Callback<Task, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty(), p.estimateProperty()};
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
        if (description.get() != null ? !description.get().equals(task.description.get()) : task.description.get() != null) return false;
        if (estimate.get() !=(task.estimate.get())) return false;
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
