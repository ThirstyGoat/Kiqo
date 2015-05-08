package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;

/**
 * Command to create a project
 *
 * @author amy
 *
 */
public class CreateProjectCommand extends Command<Project> {
    private final Project project;
    private final Organisation organisation;

    /**
     * Constructor for a command that creates a project with the specified properties
     * @param project
     * @param organisation organisation to which the project belongs
     */
    public CreateProjectCommand(final Project project, final Organisation organisation) {
       this.project = project;
        this.organisation = organisation;
    }

    @Override
    public Project execute() {
        organisation.getProjects().add(project);
        return project;
    }

    @Override
    public String toString() {
        return "<Create Project: \"" + project.getShortName() + "\" \"" + project.getLongName() + "\" \"" + project.getDescription() + "\">";
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
