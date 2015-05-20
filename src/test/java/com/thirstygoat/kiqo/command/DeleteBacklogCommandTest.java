package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Carina on 20/05/2015.
 */
public class DeleteBacklogCommandTest {
    private Backlog backlog;
    private Project project;
    private DeleteBacklogCommand command;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        backlog = new Backlog("", "", "", new Person() ,project, new ArrayList<>() );
        project.observableBacklogs().add(backlog);
        command = new DeleteBacklogCommand(backlog);
    }

    @Test
    public void deleteBacklog_BacklogRemovedFromProject() {
        Assert.assertTrue(project.getBacklogs().contains(backlog));

        command.execute();

        Assert.assertFalse(project.getBacklogs().contains(backlog));
    }

    @Test
    public void undoDeleteBacklog_BacklogAddedBackToProject() {
        Assert.assertTrue(project.getBacklogs().contains(backlog));

        command.execute();

        Assert.assertFalse(project.getBacklogs().contains(backlog));

        command.undo();

        Assert.assertTrue(project.getBacklogs().contains(backlog));
    }
}
