package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateSprintCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.StringConverters;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by leroy on 9/08/15.
 */
public class SprintDetailsPaneViewModelTest {
    private SprintDetailsPaneViewModel viewModel;
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
        viewModel = new SprintDetailsPaneViewModel();
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
        unreadyStory = new Story("unreadyStory", "", "", po, project, backlog, 666, Scale.FIBONACCI, 333, false);
        readyStory = new Story("readyStory", "", "", po, project, backlog, 420, Scale.FIBONACCI, 42, true);
        backlog.observableStories().add(readyStory);
        backlog.observableStories().add(unreadyStory);
        sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
    }

    /**
     * Populate a SprintFormViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(SprintDetailsPaneViewModel viewModel) {
        viewModel.goalProperty().set("goalShortName");
        viewModel.longNameProperty().set("goalLongName");
        viewModel.backlogProperty().set(backlog);
        viewModel.descriptionProperty().set("goalDescription");
        viewModel.releaseProperty().set(release);
        viewModel.startDateProperty().set(LocalDate.now().minusDays(11));
        viewModel.endDateProperty().set(LocalDate.now().minusDays(5));
        viewModel.teamProperty().set(team);
    }

    @Test
    public void newSprint_DoNothing_CommandTest() {
        viewModel.load(null, organisation);
        Assert.assertTrue("Command should be null if nothing was done",
                viewModel.createCommand() == null);
    }

    @Test
    public void newSprint_ValidFieldsTest() {
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
    public void existingSprint_DoNothingTest() {
        viewModel.load(sprint, organisation);
        Assert.assertTrue("Command should be null if nothing was done",
                (viewModel.createCommand() == null));
        Assert.assertTrue("All fields should be valid",
                viewModel.allValidation().isValid());
    }

    @Test
    public void existingSprint_ChangePropertyButThenUndoChange_CommandTest() {
        viewModel.load(sprint, organisation);
        String originalGoal = viewModel.goalProperty().get();
        Assert.assertTrue("Original property should not be null",
                originalGoal != null); viewModel.goalProperty().set("A different goal");
        viewModel.goalProperty().set(originalGoal);
        Assert.assertTrue("Command should be null if something was done, but then undone",
                viewModel.createCommand() == null);
    }

    @Test
    public void existingSprint_ChangeProperty_CommandTest() {
        viewModel.load(sprint, organisation);
        viewModel.goalProperty().set("A different goal");
        Command command = viewModel.createCommand();
        Assert.assertTrue("If we did something, command should not be null",
                command != null);
        Assert.assertTrue("Command should be a CompoundCommand",
                command.getClass().equals(CompoundCommand.class));
    }

    @Test
    public void existingSprint_AddValidStoryTest() {
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

//    @Test
//    public void detailsPane_UpdatedModelStringPropertyTest() {
//        viewModel.load(sprint, organisation);
//        Assert.assertTrue(viewModel.longNameProperty().get() == "sprintLongName");
//        sprint.setLongName("Jimmy Cricket");
//        Assert.assertTrue("DetailsPaneViewModel longNameProperty should update when model updated",
//                viewModel.longNameProperty().get() == "Jimmy Cricket");
//    }

//    @Test
//    public void detailsPane_UpdatedModelObjectStringPropertyTest() {
//        viewModel.load(sprint, organisation);
//        StringProperty backlogShortNameProperty = new SimpleStringProperty();
//        backlogShortNameProperty.bindBidirectional(viewModel.backlogProperty(),
//                StringConverters.backlogStringConverter(viewModel.organisationProperty()));
//        Assert.assertTrue(backlogShortNameProperty.get() == "backlog");
//        sprint.getBacklog().setShortName("New backlog shortName");
//        Assert.assertTrue("BacklogDetailsPaneViewModel should update when model object properties string property changes",
//                backlogShortNameProperty.get() == "New backlog shortName");
//    }
}
