package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.search.SearchableItems;


/**
 * Created by Bradley on 29/04/15.
 */
public class DeleteProjectCommand extends Command {
    private final Organisation organisation;
    private final Project project;

    private int index;

    /**
     *
     * @param project the team to be deleted
     * @param organisation the project to delete the team from
     */
    public DeleteProjectCommand(final Project project, final Organisation organisation) {
        this.organisation = organisation;
        this.project = project;
    }

    @Override
    public void execute() {
        index = organisation.getProjects().indexOf(project);
        organisation.getProjects().remove(project);

        // Remove from SearchableItems
        SearchableItems.getInstance().removeSearchable(project);
    }

    
    @Override
    public void undo() {
        organisation.getProjects().add(index, project);

        // Add back to SearchableItems
        SearchableItems.getInstance().addSearchable(project);
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
