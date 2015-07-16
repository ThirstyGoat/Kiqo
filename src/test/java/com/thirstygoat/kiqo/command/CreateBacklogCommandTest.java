package com.thirstygoat.kiqo.command;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;

/**
 * Created by Carina on 20/05/2015.
 */
public class CreateBacklogCommandTest {

    private Backlog backlog;
    private Project project;
    private CreateBacklogCommand command;

    @Before
    public void setup() {
        project = new Project("proj", "Project");
        backlog = new Backlog("", "", "", new Person() ,project, new ArrayList<>() );
        command = new CreateBacklogCommand(backlog);
    }

    @Test
    public void createBacklog_BacklogAddedToProject() {
        Assert.assertFalse(project.getBacklogs().contains(backlog));

        command.execute();

        Assert.assertTrue(project.getBacklogs().contains(backlog));
    }

    @Test
    public void undoCreateBacklog_BacklogRemovedFromProject() {
        command.execute();
        command.undo();

        Assert.assertFalse(project.getBacklogs().contains(backlog));
    }

}
