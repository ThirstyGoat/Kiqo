package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.create.CreateStoryCommand;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
* Created by Carina Blair on 21/07/2015.
*/
public class StoryFormViewModelTest {
    private StoryFormViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Project secondProject;
    private Release release;
    private Backlog backlog;
    private Person po;
    private Person creator;
    private Team team;
    private Story unreadyStory;
    private Story readyStory;

    @Before
    public void setup() {
        viewModel = new StoryFormViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        secondProject = new Project("projectShortName2", "projectLongName2");
        po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        creator = new Person("BillyGoat", "", "", "", "", "", "", Arrays.asList());
        backlog = new Backlog("backlog", "blLongName", "blDescription", po, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);
        release = new Release("releaseShortName", project, LocalDate.now(), "releaseDescription");
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        organisation.getPeople().add(creator);
        project.observableReleases().add(release);
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false, false, null);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true, false, null);
        project.observableUnallocatedStories().add(unreadyStory);
        project.observableUnallocatedStories().add(readyStory);
    }

    /**
     * Populate a StoryViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(StoryFormViewModel viewModel) {
        viewModel.shortNameProperty().set("storyShortName");
        viewModel.longNameProperty().set("storyLongName");
        viewModel.descriptionProperty().set("storyDescription");
        viewModel.creatorProperty().set(creator);
        viewModel.projectProperty().set(project);
        viewModel.priorityProperty().set(0);
        viewModel.scaleProperty().set(Scale.FIBONACCI);
    }

    @Test
    public void newStory_validFieldsTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);
        String errorMessages = viewModel.allValidation().getErrorMessages().toString();
        Assert.assertTrue("Fields should all be valid:\n" + errorMessages,
                viewModel.allValidation().isValid());
        Command command = viewModel.getCommand();
        Assert.assertTrue("Command should not be null if all fields are valid",
                command != null);
        Assert.assertTrue("Command should be of type CreateStoryCommand",
                command.getClass().equals(CreateStoryCommand.class));

        Exception ex = null;
        try {
            command.execute();
        } catch (Exception e) {
            ex = e;
        }
        Assert.assertTrue("Executing the command should not produce any exceptions\n" + ex,
                ex == null);
    }

    @Test
    public void existingStory_testChangeProject() {
        Story duplicateShortName = new Story();
        duplicateShortName.setShortName(readyStory.getShortName());
        secondProject.observableUnallocatedStories().add(duplicateShortName);

        viewModel.load(readyStory, organisation);
        viewModel.projectProperty().set(secondProject);
        Assert.assertFalse("Story name must be unique within a project",
                viewModel.shortNameValidation().isValid() );
    }
}
