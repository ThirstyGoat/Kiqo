package seng302.group4;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


/**
 * Created by leroy on 10/04/15.
 */
public class Release extends Item {
    private Organisation organisation;
    private StringProperty shortName;
    private StringProperty description;
    private ObjectProperty<Project> project;
    private ObjectProperty<LocalDate> date; // change back to date

    public Release(String shortName, Project project, LocalDate date, String description, Organisation organisation) {
        this.shortName = new SimpleStringProperty(shortName);
        this.project = new SimpleObjectProperty<>(project);
        this.description = new SimpleStringProperty(description);
        this.date = new SimpleObjectProperty<>(date);
        this.organisation = organisation;
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Release release = (Release) o;

        return shortName.equals(release.shortName);
    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
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

    public Project getProject() {
        return project.get();
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    @Override
    public String toString() {
        return "Release{shortName=" + shortName + ", project=" + project.get().getShortName() + ", date=" + date + ", description=" + description
                + "}";
    }
}
