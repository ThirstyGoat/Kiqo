package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.sprint.SprintViewModel;
import com.thirstygoat.kiqo.model.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;


/**
* Created by Sam Schofield on 21/07/2015.
*/
public class SprintViewModelTest {
    private SprintViewModel viewModel;
    
    @Before
    public void setUp() {
        viewModel = new SprintViewModel();
    }
    
    @Test
    public void testGoalValidator() {
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
        viewModel.longNameProperty().setValue("");
        // Cant be empty
        Assert.assertFalse(viewModel.longNameValidation().isValid());

        viewModel.longNameProperty().setValue("A long name");
        Assert.assertTrue(viewModel.longNameValidation().isValid());
    }

    @Test
    public void testDescriptionValidator() {
        //Always valid
        viewModel.descriptionProperty().setValue("");
        Assert.assertTrue(viewModel.descriptionValidation().isValid());

        viewModel.descriptionProperty().setValue("A description");
        Assert.assertTrue(viewModel.descriptionValidation().isValid());
    }

    @Test
    public void testStartDateValidator() {
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
        Assert.assertTrue("Sprint can have no stories",
                viewModel.storiesValidation().isValid());

        Story unreadyStory = new Story();
        unreadyStory.setIsReady(false);

        Story readyStory = new Story();
        readyStory.setIsReady(true);

        viewModel.stories().add(readyStory);
        Assert.assertTrue("Sprint can have ready stories",
                viewModel.storiesValidation().isValid());

        viewModel.stories().add(unreadyStory);
        Assert.assertFalse("Sprint cannot have un-ready stories",
                viewModel.storiesValidation().isValid());

        viewModel.stories().remove(unreadyStory);
        Assert.assertTrue("Sprint can have ready stories",
                viewModel.storiesValidation().isValid());
    }

    @Test
    public void testTeamValidation() {
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
    public void testReleaseValidation() {
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
