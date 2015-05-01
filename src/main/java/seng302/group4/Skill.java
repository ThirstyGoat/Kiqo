package seng302.group4;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Skill skill = (Skill) o;

        if (!getShortName().equals(skill.getShortName())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return getShortName().hashCode();
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
    public String getShortName() {
        return shortName.get();
    }

    @Override
    public StringProperty shortNameProperty() {
        return shortName;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getDescription() {
        return description.get();
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }
}
