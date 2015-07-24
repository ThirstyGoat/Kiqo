package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Bradley on 9/04/15.
 */
public class DeleteAcceptanceCriteriaCommandTest {
    private Project project;
    private Backlog backlog;
    private Person person;
    private Story story;
    private DeleteAcceptanceCriteriaCommand command;
    private AcceptanceCriteria acceptanceCriteria;

    @Before
    public void setup() {
        project = new Project("proj1", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<Skill>());
        story = new Story("story1", "Story", "descr", person, project, backlog, 9, Scale.FIBONACCI, 0, false);
        acceptanceCriteria = new AcceptanceCriteria("Creating new acceptance criteria will add it to the list of AC's in the story");
        story.getAcceptanceCriteria().add(acceptanceCriteria);
        command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story);
    }

    @Test
    public void deleteAC() {
        Assert.assertTrue(story.getAcceptanceCriteria().contains(acceptanceCriteria));

        command.execute();

        Assert.assertFalse(story.getAcceptanceCriteria().contains(acceptanceCriteria));
    }

    @Test
    public void undoDeleteAC() {
        Assert.assertTrue(story.getAcceptanceCriteria().contains(acceptanceCriteria));

        command.execute();

        Assert.assertFalse(story.getAcceptanceCriteria().contains(acceptanceCriteria));

        command.undo();

        Assert.assertTrue(story.getAcceptanceCriteria().contains(acceptanceCriteria));
    }
}
