package seng302.group4;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by samschofield on 22/04/15.
 */
public class Project extends Item {
    private String shortName;
    private String longName;
    private String description;
    private final ArrayList<Release> releases = new ArrayList<>();

    /**
     * Create new Project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     */
    public Project(final String shortName, final String longName) {
        this.shortName = shortName;
        this.longName = longName;
    }

    /**
     * Create a new project
     *
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param description description of the project
     */
    public Project(final String shortName, final String longName, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
    }

    /**
     *
     * @return Description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description Description of the project
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return longName Long name of the project
     */
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @param longName Long name for the project
     */
    public void setLongName(final String longName) {
        this.longName = longName;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     *
     * @param shortName Short name of the project
     */
    public void setShortName(final String shortName) {
        this.shortName = shortName;
    }

    public List<Release> getReleases() {
        return releases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Project project = (Project) o;

        return shortName.equals(project.shortName);

    }

    @Override
    public int hashCode() {
        return shortName.hashCode();
    }

    @Override
    public String toString() {
        return "Project{" +
                "shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
