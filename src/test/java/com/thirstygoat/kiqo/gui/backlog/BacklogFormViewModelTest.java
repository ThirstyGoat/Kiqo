package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateBacklogCommand;
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
public class BacklogFormViewModelTest {
    private BacklogFormViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Person po;
    private Team team;
    private Story unreadyStory;
    private Story readyStory;

    @Before
    public void setup() {
        viewModel = new BacklogFormViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        po = new Person("PO", "", "", "", "", "", "", Arrays.asList(organisation.getPoSkill()));
        backlog = new Backlog("backlog", "blLongName", "blDescription", po, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);
        release = new Release("releaseShortName", project, LocalDate.now(), "releaseDescription");
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(po);
        project.observableReleases().add(release);
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false, false);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true, false);
        project.observableUnallocatedStories().add(unreadyStory);
        project.observableUnallocatedStories().add(readyStory);

        viewModel.setExitStrategy(() -> {}); // not running in a JavaFX thread so no special exit action needed
    }

    /**
     * Populate a BacklogViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(BacklogFormViewModel viewModel) {
        viewModel.shortNameProperty().set("backlogShortName");
        viewModel.longNameProperty().set("backlogLongNameProperty");
        viewModel.productOwnerProperty().set(po);
        viewModel.projectProperty().set(project);
        viewModel.scaleProperty().set(Scale.FIBONACCI);
    }

    @Test
    public void newBacklog_DoNothing_CommandTest() {
        viewModel.load(null, organisation);
        Assert.assertTrue("Command should be null if nothing was done",
                viewModel.getCommand() == null);
    }

    @Test
    public void newBacklog_ValidFieldsTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);
        String errorMessages = viewModel.allValidation().getErrorMessages().toString();
        Assert.assertTrue("Fields should all be valid:\n" + errorMessages,
                viewModel.allValidation().isValid());
        Command command = viewModel.getCommand();
        Assert.assertTrue("Command should not be null if all fields are valid",
                command != null);
        Assert.assertTrue("Command should be of type CreateBacklogCommand",
                command.getClass().equals(CreateBacklogCommand.class));

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
    public void existingBacklog_EditingShortNameToBeSameAsSelfTest() {
        Backlog backlog = new Backlog("aBacklog", "", "", po, project, new ArrayList<>(), Scale.FIBONACCI);
        CreateBacklogCommand command = new CreateBacklogCommand(backlog);
        command.execute();

        viewModel.load(null, organisation);
        viewModel.shortNameProperty().set("aBacklog");
        Assert.assertTrue("The backlog short name should be valid when no project is selected",
                viewModel.shortNameValidation().isValid());

        viewModel.projectProperty().set(project);
        Assert.assertFalse("The backlog short name should NOT be valid when a project is selected which has a backlog" +
                " with the same short name", viewModel.shortNameValidation().isValid());

        viewModel.projectProperty().set(null);
        Assert.assertTrue("The backlog short name should be valid when no project is selected",
                viewModel.shortNameValidation().isValid());

        BacklogFormViewModel editingViewModel = new BacklogFormViewModel();
        editingViewModel.load(backlog, organisation);
        viewModel.shortNameProperty().set("aBacklog");
        Assert.assertTrue("Editing the backlog should allow the same short name to be set as itself",
                editingViewModel.shortNameValidation().isValid());
    }

    @Test
    public void existingBacklog_settingStoriesTest() {
        viewModel.load(backlog, organisation);
        viewModel.stories().add(unreadyStory);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());

        Assert.assertTrue("backlog should contain a story",
                backlog.getStories().contains(unreadyStory));
    }

    @Test
    public void existingBacklog_existingStoriesLoadTest() {
        backlog.observableStories().add(unreadyStory);
        backlog.observableStories().add(readyStory);
        viewModel.load(backlog, organisation);
        Assert.assertTrue("ViewModel stories property should contain two items",
                viewModel.stories().size() == 2);
    }

    @Test
    public void existingBacklog_storyChangeCommandTest() {
        backlog.observableStories().add(readyStory);

        viewModel.load(backlog, organisation);
        Assert.assertTrue(viewModel.stories().contains(readyStory));

        viewModel.stories().remove(readyStory);
        viewModel.okAction();
        Assert.assertFalse(backlog.getStories().contains(readyStory));

        UndoManager.getUndoManager().undoCommand();
        Assert.assertTrue(backlog.getStories().contains(readyStory));
        Assert.assertTrue(viewModel.stories().contains(readyStory));
        Assert.assertTrue(viewModel.stories().size() == 1);
    }
}
