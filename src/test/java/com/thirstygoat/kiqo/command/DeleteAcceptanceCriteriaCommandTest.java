package com.thirstygoat.kiqo.command;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Story;

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
        project = new Project("proj", "Project");
        person = new Person("pers1", "Person","descr", "id", "email", "phone", "dept", new ArrayList<Skill>());
        story = new Story("story1", "Story One", "descr", person, project, backlog, 9);
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
