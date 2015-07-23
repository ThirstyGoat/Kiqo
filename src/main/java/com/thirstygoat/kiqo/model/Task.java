package com.thirstygoat.kiqo.model;

import javafx.beans.property.*;

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
        estimate = new SimpleFloatProperty();
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

    public float getEstime() {
        return estimate.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    @Override
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return description;
    }
}
