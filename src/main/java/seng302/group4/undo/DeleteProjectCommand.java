package seng302.group4.undo;

import seng302.group4.Organisation;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;

import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Created by Bradley on 29/04/15.
 */
public class DeleteProjectCommand extends Command<Project> {
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
    public Project execute() {
        index = organisation.getProjects().indexOf(project);
        organisation.getProjects().remove(project);
        return project;
    }

    
    @Override
    public void undo() {
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
