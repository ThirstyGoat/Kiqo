package com.thirstygoat.kiqo.model;

import javafx.beans.property.*;

/**
 * Created by leroy on 15/05/15.
 */
public class Story extends Item {
    private static final int DEFAULT_PRIORITY = 0;

    private StringProperty shortName;
    private StringProperty longName;
    private StringProperty description;
    private ObjectProperty<Person> creator;
    private IntegerProperty priority;

    /**
     * no-arg constructor for JavaBeans compliance
     */
    public Story() {
        this("", "", "", null, DEFAULT_PRIORITY);
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

    public IntegerProperty priorityProperty() {
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
