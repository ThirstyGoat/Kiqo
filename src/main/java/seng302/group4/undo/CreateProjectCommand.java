package seng302.group4.undo;

import seng302.group4.Project;

import java.io.File;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateProjectCommand extends Command<Project> {
    private final String shortName;
    private final String longName;
    private final String description;
    private final File saveLocation;
    private Project project = null;

    /**
     * Constructor for a command that creates a project with the specified
     * properties (and null description)
     *
     * @param shortName
     * @param longName
     * @param saveLocation
     */
    public CreateProjectCommand(final String shortName, final String longName, final File saveLocation) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = null;
        this.saveLocation = saveLocation;
    }

    /**
     * Constructor for a command that creates a project with the specified
     * properties
     *
     * @param shortName
     * @param longName
     * @param saveLocation
     * @param description
     */
    public CreateProjectCommand(final String shortName, final String longName, final File saveLocation, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.saveLocation = saveLocation;
    }

    @Override
    public Project execute() {
        if (this.project == null) {
            this.project = new Project(this.shortName, this.longName, this.saveLocation, this.description);
        }
        return this.project;
    }

    @Override
    public String toString() {
        return "<Create Project: \"" + this.shortName + "\" \"" + this.longName + "\" \"" + this.saveLocation + "\" \"" + this.description
                + "\">";
    }

    @Override
    public void undo() {
        this.project.prepareForDestruction();
    }
}
