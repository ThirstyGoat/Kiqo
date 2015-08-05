package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateProjectCommand extends CreateCommand {
    private final Project project;
    private final Organisation organisation;

    /**
     * Constructor for a command that creates a project with the specified properties
     * @param project project to be added to the model
     * @param organisation organisation to which the project belongs
     */
    public CreateProjectCommand(final Project project, final Organisation organisation) {
        super(project);
        this.project = project;
        this.organisation = organisation;
    }

    @Override
    public void addToModel() {
        organisation.getProjects().add(project);
    }

    @Override
    public void removeFromModel() {
        // Goodbye team
        organisation.getProjects().remove(project);
    }

    @Override
    public String getType() {
        return "Create Project";
    }

    @Override
    public String toString() {
        return "<Create Project: \"" + project.getShortName() + "\" \"" + project.getLongName() + "\" \"" + project.getDescription() + "\">";
    }

}
