package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.create.CreateSprintCommand;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leroy on 9/08/15.
 */
public class SprintViewModelInteractionTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private SprintViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Story unreadyStory;
    private Story readyStory;
    private Sprint sprint;
    private Person po;
    private Team team;

    @Before
    public void setup() {
        viewModel = new SprintViewModel();
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
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false, false, null);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true, false, null);
        backlog.getStories().add(readyStory);
        backlog.getStories().add(unreadyStory);
        sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
    }

    /**
     * Populate a SprintFormViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(SprintViewModel viewModel) {
        viewModel.goalProperty().set("goalShortName");
        viewModel.longNameProperty().set("goalLongName");
        viewModel.backlogProperty().set(backlog);
        // viewModel.descriptionProperty().set("goalDescription"); // Description is optional
        viewModel.releaseProperty().set(release);
        viewModel.startDateProperty().set(LocalDate.now().minusDays(11));
        viewModel.endDateProperty().set(LocalDate.now().minusDays(5));
        viewModel.teamProperty().set(team);
    }

    @Test
    public void newSprint_doNothing_CommandTest() {
        viewModel.load(null, organisation);
        Assert.assertTrue("Command should be null if nothing was done", viewModel.createCommand() == null);
    }

    @Test
    public void newSprint_validFieldsTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);
        String errorMessages = viewModel.allValidation().getErrorMessages().toString();
        Assert.assertTrue("Fields should all be valid:\n" + errorMessages,
                viewModel.allValidation().isValid());
        Command command = viewModel.createCommand();
        Assert.assertTrue("Command should not be null if all fields are valid",
                command != null);
        Assert.assertTrue("Command should be of type CreatSprintCommand",
                        command.getClass().equals(CreateSprintCommand.class));
        command.execute();
    }

    @Test
    public void existingSprint_doNothingTest() {
        viewModel.load(sprint, organisation);
        Assert.assertTrue("Command should be null if nothing was done",
                (viewModel.createCommand() == null));
        Assert.assertTrue("All fields should be valid", viewModel.allValidation().isValid());
    }

    @Test
    public void existingSprint_changePropertyButThenUndoChange_CommandTest() {
        viewModel.load(sprint, organisation);
        String originalGoal = viewModel.goalProperty().get();
        Assert.assertTrue("Original property should not be null", originalGoal != null); viewModel.goalProperty().set(
                        "A different goal");
        viewModel.goalProperty().set(originalGoal);
        Assert.assertTrue("Command should be null if something was done, but then undone", viewModel.createCommand() == null);
    }

    @Test
    public void existingSprint_changeProperty_CommandTest() {
        viewModel.load(sprint, organisation);
        viewModel.goalProperty().set("A different goal");
        Command command = viewModel.createCommand();
        Assert.assertTrue("If we did something, command should not be null",
                command != null);
        Assert.assertTrue("Command should be a CompoundCommand",
                command.getClass().equals(CompoundCommand.class));
    }

    @Test
    public void existingSprint_addValidStoryTest() {
        viewModel.load(sprint, organisation);
        viewModel.stories().add(readyStory);
        Assert.assertTrue("There should be no validation errors",
                viewModel.allValidation().isValid());
        Command command = viewModel.createCommand();
        Assert.assertTrue("Something changed, so command should not be null",
                command != null);
        Assert.assertTrue("Command should be a CompoundCommand",
                command.getClass().equals(CompoundCommand.class));
        command.execute();
        Assert.assertTrue("Sprint should now have story",
                sprint.getStories().contains(readyStory));
    }

    @Test
    public void newSprint_optionalFieldNullTest() {
        // Test that if optional fields are left blank that they are set to some sane default value rather than null
        viewModel.load(null, organisation);
        populateFields(viewModel);
        viewModel.createCommand().execute();
        Sprint newSprint = viewModel.sprintProperty().get();
        SprintViewModel newViewModel = new SprintViewModel();
        newViewModel.load(newSprint, organisation);
        Assert.assertFalse("Description should not be null",
                newViewModel.descriptionProperty().get() == null);
        Assert.assertTrue("Description should be empty string",
                newViewModel.descriptionProperty().get() == "");
    }

    @Test
    public void newSprint_goalTest() {
        // Test that if optional fields are left blank that they are set to some sane default value rather than null
        Sprint sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
        CreateSprintCommand command = new CreateSprintCommand(sprint);
        command.execute();

        viewModel.load(null, organisation);
        viewModel.goalProperty().set("sprintGoal");
        Assert.assertTrue("The sprint shortname should be valid when no release is selected", viewModel.goalValidation().isValid());

        viewModel.releaseProperty().set(release);
        Assert.assertFalse("The sprint shortname should NOT be valid when a release is selected that contains a sprint" +
                " with the same shortname", viewModel.goalValidation().isValid());

        viewModel.releaseProperty().set(null);
        Assert.assertTrue("The sprint shortname should be valid when no release is selected", viewModel.goalValidation().isValid());
    }

    @Test
    public void existingSprint_editingGoalNameToBeSameAsSelfTest() {
        // Test that a goal having the same shortName does not cause goalValidation to be invalid.
        Sprint sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
        CreateSprintCommand command = new CreateSprintCommand(sprint);
        command.execute();

        viewModel.load(null, organisation);
        viewModel.goalProperty().set("sprintGoal");
        Assert.assertTrue("The sprint shortname should be valid when no release is selected", viewModel.goalValidation().isValid());

        viewModel.releaseProperty().set(release);
        Assert.assertFalse("The sprint shortname should NOT be valid when a release is selected that contains a sprint" +
                " with the same shortname", viewModel.goalValidation().isValid());

        viewModel.releaseProperty().set(null);
        Assert.assertTrue("The sprint shortname should be valid when no release is selected", viewModel.goalValidation().isValid());

        SprintViewModel editingViewModel = new SprintViewModel();
        editingViewModel.load(sprint, organisation);
        viewModel.goalProperty().set("sprintGoal");
        Assert.assertTrue("Editing the sprint allow the shortName to be set to the same as itself",
                editingViewModel.goalValidation().isValid());
    }
}
