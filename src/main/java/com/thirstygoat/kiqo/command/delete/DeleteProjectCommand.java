package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;


/**
 * Created by Bradley on 29/04/15.
 */
public class DeleteProjectCommand extends DeleteCommand {
    private final Organisation organisation;
    private final Project project;

    private int index;

    /**
     *
     * @param project the team to be deleted
     * @param organisation the project to delete the team from
     */
    public DeleteProjectCommand(final Project project, final Organisation organisation) {
        super(project);
        this.organisation = organisation;
        this.project = project;
    }

    @Override
    public void removeFromModel() {
        index = organisation.getProjects().indexOf(project);
        organisation.getProjects().remove(project);
    }

    
    @Override
    public void addToModel() {
        organisation.getProjects().add(index, project);
    }

    @Override
    public String toString() {
        return "<Delete Project: \"" + project.getShortName() + "\">";
    }

    @Override
    public String getType() {
        return "Delete Project";
    }
}
