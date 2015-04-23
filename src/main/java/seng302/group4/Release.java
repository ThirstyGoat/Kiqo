package seng302.group4;

import java.time.LocalDate;


/**
 * Created by leroy on 10/04/15.
 */
public class Release extends Item{
    private Organisation organisation;
    private String shortName;
    private Project project;
    private LocalDate date; // change back to date
    private String description;

    public Release(String shortName, Project project, LocalDate date, String description, Organisation organisation) {
        this.shortName = shortName;
        this.project = project;
        this.description = description;
        this.date = date;
        this.organisation = organisation;
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
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    @Override
    public String toString() {
        return "Release{shortName=" + shortName + ", project=" + project.getShortName() + ", date=" + date + ", description=" + description
                + "}";
    }
}
