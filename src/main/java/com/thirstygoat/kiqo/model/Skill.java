package com.thirstygoat.kiqo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by leroy on 25/03/15.
 */
public class Skill extends Item {
    private final StringProperty shortName;
    private final StringProperty description;

    /**
     * No-args constructor for JavaBeans(TM) compliance. Use at your own risk.
     */
    public Skill() {
        shortName = new SimpleStringProperty();
        description = new SimpleStringProperty();
    }

    public Skill(String shortName, String description) {
        this.shortName = new SimpleStringProperty(shortName);
        this.description = new SimpleStringProperty(description);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Skill{shortName=" + getShortName() + ", description=" + getDescription() + "}";
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
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

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
