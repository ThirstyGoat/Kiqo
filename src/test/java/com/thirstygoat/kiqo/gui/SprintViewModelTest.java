package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.gui.sprint.SprintViewModel;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;


/**
* Created by Sam Schofield on 21/07/2015.
*/
public class SprintViewModelTest {

    @Test
    public void initialTest() {
        // Test initial values before anything has been added.
        SprintViewModel viewModel = new SprintViewModel();

        // Assert False should be invalid by default.
        // Assert True should be valid by default.
        Assert.assertFalse(viewModel.goalValidation().isValid());
        Assert.assertTrue(viewModel.descriptionValidation().isValid());
        Assert.assertFalse(viewModel.startDateValidation().isValid());
        Assert.assertFalse(viewModel.endDateValidation().isValid());
        Assert.assertFalse(viewModel.backlogValidation().isValid());
        Assert.assertFalse(viewModel.releaseValidation().isValid());
        Assert.assertFalse(viewModel.teamValidation().isValid());
        Assert.assertTrue(viewModel.storiesValidation().isValid());
    }

    @Test
    public void testGoalValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        viewModel.goalProperty().setValue("");
        // Can't be empty
        Assert.assertFalse(viewModel.goalValidation().isValid());

        viewModel.goalProperty().setValue("Sprint1");
        // can be non empty and unique
        Assert.assertTrue(viewModel.goalValidation().isValid());

        Project project = new Project();
        viewModel.backlogProperty().setValue(new Backlog("backlog", null, null, null, project,  new ArrayList<Story>(), null));
        Sprint sprint = new Sprint();
        sprint.setGoal("Sprint");
        viewModel.backlogProperty().get().getProject().observableSprints().add(sprint);
        viewModel.goalProperty().set("Sprint");
        // Must be unique
        Assert.assertFalse(viewModel.goalValidation().isValid());

        viewModel.goalProperty().set("Must be less than 20 characters");
        Assert.assertFalse(viewModel.goalValidation().isValid());
    }

    @Test
    public void testLongNameValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        viewModel.longNameProperty().setValue("");
        // Cant be empty
        Assert.assertFalse(viewModel.longNameValidation().isValid());

        viewModel.longNameProperty().setValue("A long name");
        Assert.assertTrue(viewModel.longNameValidation().isValid());
    }

    @Test
    public void testDescriptionValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        //Always valid
        viewModel.descriptionProperty().setValue("");
        Assert.assertTrue(viewModel.descriptionValidation().isValid());

        viewModel.descriptionProperty().setValue("A description");
        Assert.assertTrue(viewModel.descriptionValidation().isValid());
    }

    @Test
    public void testStartDateValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        // empty is invalid
        Assert.assertFalse(viewModel.startDateValidation().isValid());

        viewModel.endDateProperty().set(LocalDate.now());
        Assert.assertFalse(viewModel.startDateValidation().isValid());

        viewModel.endDateProperty().set(null);
        viewModel.startDateProperty().setValue(LocalDate.now());
        Assert.assertTrue(viewModel.startDateValidation().isValid());

        viewModel.endDateProperty().setValue(LocalDate.now().minusDays(2));
        Assert.assertFalse(viewModel.startDateValidation().isValid());
    }

    @Test
    public void testEndDateValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        Assert.assertFalse("Null end date is invalid",
                viewModel.endDateValidation().isValid());

        viewModel.endDateProperty().set(LocalDate.now().plusDays(3));
        viewModel.releaseProperty().set(new Release("", null, LocalDate.now().plusDays(2), ""));
        Assert.assertFalse("End date must be before release date",
                viewModel.endDateValidation().isValid());
        viewModel.endDateProperty().set(LocalDate.now().plusDays(2));
        Assert.assertTrue("End date same as release date is valid",
                viewModel.endDateValidation().isValid());

        viewModel.releaseProperty().set(new Release("", null, LocalDate.now(), ""));
        Assert.assertFalse("Release date must not be before end date",
                viewModel.endDateValidation().isValid());
    }

    @Test
    public void testStoriesValidator() {
        SprintViewModel viewModel = new SprintViewModel();

        Assert.assertTrue("Sprint can have no stories",
                viewModel.storiesValidation().isValid());

        Story unreadyStory = new Story();
        unreadyStory.setIsReady(false);

        Story readyStory = new Story();
        readyStory.setIsReady(true);

        viewModel.getStories().add(readyStory);
        Assert.assertTrue("Sprint can have ready stories",
                viewModel.storiesValidation().isValid());

        viewModel.getStories().add(unreadyStory);
        Assert.assertFalse("Sprint cannot have un-ready stories",
                viewModel.storiesValidation().isValid());

        viewModel.getStories().remove(unreadyStory);
        Assert.assertTrue("Sprint can have ready stories",
                viewModel.storiesValidation().isValid());
    }

    @Test
    public void testTeamValidation() {
        SprintViewModel viewModel = new SprintViewModel();

        Assert.assertFalse("Should not be valid by default",
                viewModel.teamValidation().isValid());

        viewModel.teamProperty().set(null);
        Assert.assertFalse("Null team is not valid",
                viewModel.teamValidation().isValid());

        viewModel.teamProperty().set(new Team());
        Assert.assertTrue("Non null team is valid",
                viewModel.teamValidation().isValid());
    }

    @Test
    public void testBacklogValidation() {
        SprintViewModel viewModel = new SprintViewModel();

        Assert.assertFalse("Should not be valid by default",
                viewModel.backlogValidation().isValid());

        viewModel.backlogProperty().set(null);
        Assert.assertFalse("Null backlog is not valid",
                viewModel.backlogValidation().isValid());

        viewModel.backlogProperty().set(new Backlog());
        Assert.assertTrue("Non null backlog is valid",
                viewModel.backlogValidation().isValid());
    }

    @Test
    public void releaseValidationTest() {
        SprintViewModel viewModel = new SprintViewModel();

        Assert.assertFalse("Should not be valid by default",
                viewModel.releaseValidation().isValid());

        viewModel.backlogProperty().set(null);
        Assert.assertFalse("Null release is not valid",
                viewModel.releaseValidation().isValid());

        viewModel.releaseProperty().set(new Release("", new Project(), LocalDate.now(), ""));
        Assert.assertTrue("Non null release is valid",
                viewModel.releaseValidation().isValid());
    }
}
