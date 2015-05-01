package seng302.group4.undo;

import seng302.group4.Organisation;
import seng302.group4.Project;

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
    private final Organisation organisation;
    private Project project = null;

    /**
     * Constructor for a command that creates a project with the specified properties
     *
     * @param shortName non-null unique ID for display
     * @param longName more detailed name
     * @param description Extended description of the project
     * @param organisation organisation to which the project belongs
     */
    public CreateProjectCommand(final String shortName, final String longName, final String description,
                                final Organisation organisation) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
        this.organisation = organisation;
    }

    @Override
    public Project execute() {
        if (project == null) {
            project = new Project(shortName, longName, description);
        }
        organisation.getProjects().add(project);
        return project;
    }

    @Override
    public String toString() {
        return "<Create Project: \"" + shortName + "\" \"" + longName + "\" \"" + description + "\">";
    }

    @Override
    public void undo() {
        // Goodbye team
        organisation.getProjects().remove(project);
    }

    @Override
    public String getType() {
        return "Create Project";
    }

}
