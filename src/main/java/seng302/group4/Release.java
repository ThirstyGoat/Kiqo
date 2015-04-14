package seng302.group4;

import java.time.LocalDate;


/**
 * Created by leroy on 10/04/15.
 */
public class Release {
    private Project project;
    private String shortName;
    private String description;
    private LocalDate date;  //change back to date

    public Release(String shortName, LocalDate date, String description, Project project) {
        this.shortName = shortName;
        this.description = description;
        this.date = date;
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Release release = (Release) o;

        return shortName.equals(release.shortName);
    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String toString() {
        return shortName;
    }
}
