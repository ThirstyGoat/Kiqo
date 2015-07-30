package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.gui.model.SprintViewModel;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.util.TestUtils;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.function.Predicate;


/**
* Created by Sam Schofield on 21/07/2015.
*/
public class SprintViewModelTest {

    @Test
    public void testGoalValidator() {
        SprintViewModel viewModel = new SprintViewModel();
        FunctionBasedValidator<String> predicate = viewModel.getGoalValidator();

        viewModel.goalProperty().setValue("");
        // Cant be empty
        Assert.assertFalse(predicate.getValidationStatus().isValid());

        viewModel.goalProperty().setValue("Sprint1");
        // can be non empty and unique
        Assert.assertTrue(predicate.getValidationStatus().isValid());

        viewModel.projectProperty().setValue(TestUtils.initProject());

        Sprint sprint = new Sprint();
        sprint.setGoal("Sprint");
        viewModel.projectProperty().get().observableSprints().add(sprint);
        viewModel.goalProperty().set("Sprint");
        // Must be unique
        Assert.assertFalse(predicate.getValidationStatus().isValid());

        viewModel.goalProperty().set("Must be less than 20 characters");
        Assert.assertFalse(predicate.getValidationStatus().isValid());
    }

    @Test
    public void testLongNameValidator() {
        SprintViewModel viewModel = new SprintViewModel();
        FunctionBasedValidator<String> predicate = viewModel.getLongNameValidator();

        viewModel.longNameProperty().setValue("");
        // Cant be empty
        Assert.assertFalse(predicate.getValidationStatus().isValid());

        viewModel.longNameProperty().setValue("A long name");
        Assert.assertTrue(predicate.getValidationStatus().isValid());
    }

    @Test
    public void testDescriptionValidator() {
        SprintViewModel viewModel = new SprintViewModel();
        FunctionBasedValidator<String> predicate = viewModel.getDescriptionValidator();

        //Always valid
        viewModel.descriptionProperty().setValue("");
        Assert.assertTrue(predicate.getValidationStatus().isValid());

        viewModel.descriptionProperty().setValue("A description");
        Assert.assertTrue(predicate.getValidationStatus().isValid());
    }

    @Test
    public void testStartDateValidator() {
        SprintViewModel viewModel = new SprintViewModel();
        FunctionBasedValidator<LocalDate> predicate = viewModel.getStartDateValidator();

        // empty is invalid
        Assert.assertFalse(predicate.getValidationStatus().isValid());

        viewModel.startDateProperty().setValue(LocalDate.now());
        Assert.assertTrue(predicate.getValidationStatus().isValid());

        viewModel.endDateProperty().setValue(LocalDate.now().minusDays(2));
        System.out.println(predicate.getValidationStatus().isValid());
    }






}
