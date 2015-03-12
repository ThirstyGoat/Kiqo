package seng302.group4;

/**
 * Created by samschofield on 12/03/15.
 * Project class represents a software project
 */
public class Project {
    private String shortName;
    private String longName;
    private String description;
    private String saveLocation;

    /**
     * Create new Project
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param saveLocation save location for the project
     */
    public Project(String shortName, String longName, String saveLocation) {
        this.shortName = shortName;
        this.longName = longName;
        this.saveLocation = saveLocation;
    }

    /**
     * Create a new project
     * @param shortName a unique short name for the project
     * @param longName long name for project
     * @param saveLocation save location for the project
     * @param description description of the project
     */
    public Project(String shortName, String longName, String saveLocation, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.saveLocation = saveLocation;
    }

    /**
     *
     * @return the short name of project
     */
    public String getShortName() {
        return shortName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Project project = (Project) o;

        if (!shortName.equals(project.shortName)) return false;

        return true;
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
                ", saveLocation='" + saveLocation + '\'' +
                '}';
    }

    /**
     *
     * @param shortName the short name of the project
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     *
     * @return longName the long name of the project
     */
    public String getLongName() {
        return longName;
    }

    /**
     *
     * @param longName the long name for the project
     */
    public void setLongName(String longName) {
        this.longName = longName;
    }

    /**
     *
     * @return the description of the project
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description the description of the project
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return save location of project
     */
    public String getSaveLocation() {
        return saveLocation;
    }

    /**
     *
     * @param saveLocation save location of project
     */
    public void setSaveLocation(String saveLocation) {
        this.saveLocation = saveLocation;
    }
}
