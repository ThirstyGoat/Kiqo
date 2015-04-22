package seng302.group4.undo;

import java.io.File;

import seng302.group4.Organisation;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateProjectCommand extends Command<Organisation> {
    private final String shortName;
    private final String longName;
    private final String description;
    private final File saveLocation;
    private Organisation organisation = null;

    /**
     * Constructor for a command that creates a project with the specified properties and an empty description
     *
     * @param shortName non-null unique ID for display
     * @param longName more detailed name
     * @param saveLocation where project will be serialised to disk
     */
    public CreateProjectCommand(final String shortName, final String longName, final File saveLocation) {
        this(shortName, longName, saveLocation, null);
    }

    /**
     * Constructor for a command that creates a project with the specified properties
     *
     * @param shortName non-null unique ID for display
     * @param longName more detailed name
     * @param saveLocation where project will be serialised to disk
     * @param description Extended description of the project
     */
    public CreateProjectCommand(final String shortName, final String longName, final File saveLocation, final String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.saveLocation = saveLocation;
    }

    @Override
    public Organisation execute() {
        if (organisation == null) {
            organisation = new Organisation(shortName, longName, saveLocation, description);
        }
        return organisation;
    }

    @Override
    public String toString() {
        return "<Create Project: \"" + shortName + "\" \"" + longName + "\" \"" + saveLocation + "\" \"" + description
                + "\">";
    }

    @Override
    public void undo() {
        // FIXME implement properly
        organisation.prepareForDestruction();
    }

    @Override
    public String getType() {
        return "Create Project";
    }

}
