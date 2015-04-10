package seng302.group4;

import java.util.Date;

/**
 * Created by leroy on 10/04/15.
 */
public class Release {
    private Project project;
    private String id;
    private String description;
    private Date date;

    public Release(String id, String description, Date date, Project project) {
        this.id = id;
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

        return id.equals(release.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String toString() {
        return id;
    }
}
