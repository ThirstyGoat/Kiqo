package com.thirstygoat.kiqo.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by leroy on 15/05/15.
 */
public class Story extends Item {
    private static final int DEFAULT_PRIORITY = 0;

    private SimpleStringProperty shortName;
    private SimpleStringProperty longName;
    private SimpleStringProperty description;
    private ObjectProperty<Person> creator;
    private SimpleIntegerProperty priority;

    /**
     * no-arg constructor for JavaBeans compliance
     */
    public Story() {
        this.shortName = new SimpleStringProperty("");
        this.longName = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.creator = null;
        this.priority = new SimpleIntegerProperty(DEFAULT_PRIORITY);
    }

    public Story(String shortName, String longName, String description, Person creator, Integer priority) {
        this.shortName = new SimpleStringProperty(shortName);
        this.longName = new SimpleStringProperty(longName);
        this.description = new SimpleStringProperty(description);
        this.creator = new SimpleObjectProperty<>(creator);
        this.priority = new SimpleIntegerProperty(priority);
    }

    public String getShortName() {
        return shortName.get();
    }

    public SimpleStringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public SimpleStringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public SimpleStringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getCreator() {
        return creator.get();
    }

    public ObjectProperty<Person> creatorProperty() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator.set(creator);
    }

    public int getPriority() {
        return priority.get();
    }

    public SimpleIntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    @Override
    public int hashCode() {
        return getShortName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Story)) {
            return false;
        }
        final Story other = (Story) obj;
        if (getShortName() == null) {
            if (other.getShortName() != null) {
                return false;
            }
        } else if (!getShortName().equals(other.getShortName())) {
            return false;
        }
        return true;
    }

}
