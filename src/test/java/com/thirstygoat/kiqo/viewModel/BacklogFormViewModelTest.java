package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

/**
* Created by Carina Blair on 21/07/2015.
*/
public class BacklogFormViewModelTest {

    @Test
    public void testShortNameValidation() throws NoSuchFieldException, IllegalAccessException {

        BacklogFormViewModel backlogFormViewModel = new BacklogFormViewModel();
        Organisation organisation = new Organisation();
        Predicate<String> predicate = backlogFormViewModel.getShortNameValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(backlogFormViewModel.shortNameProperty().get()));
        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
        Assert.assertFalse("Must not be longer than 20 characters.", predicate.test("This name is longer than 20 characters."));

        // validating uniqueness within project requires model data
        final String projectName = "project shortName";
        final String backlogName = "backlog shortName";

        Person productOwner = new Person("shortName", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
        Project project = new Project(projectName, "longName");

        // set project field
        backlogFormViewModel.projectNameProperty().set(projectName);

        Assert.assertTrue("Unique short name not recognised as valid.", predicate.test("unique"));
        Assert.assertFalse("Must be unique within project.", predicate.test(backlogName));
    }

    @Test
    public void testLongNameValidation() {
        BacklogFormViewModel backlogFormViewModel = new BacklogFormViewModel();
        Predicate<String> predicate = backlogFormViewModel.getLongNameValidation();

        Assert.assertFalse("Must not be valid initially.", 
                predicate.test(backlogFormViewModel.longNameProperty().get()));

        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
    }

    @Test
    public void testDescriptionProperty() {
        BacklogFormViewModel backlogFormViewModel = new BacklogFormViewModel();
        Predicate<String> predicate = backlogFormViewModel.getDescriptionValidation();

        Assert.assertTrue("Description should be valid by default.", predicate.test(backlogFormViewModel.descriptionProperty().get()));
        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertTrue("Empty string not recognised as valid.", predicate.test(""));
    }

    @Test
    public void testProductOwnerTest() {
        BacklogFormViewModel backlogFormViewModel = new BacklogFormViewModel();
        Organisation organisation = new Organisation();
        backlogFormViewModel.setOrganisation(organisation);

        Predicate<String> predicate = backlogFormViewModel.getProductOwnerValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(backlogFormViewModel.productOwnerNameProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));

        Person productOwner = new Person("shortName", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
        Assert.assertFalse("Product Owner must exist in organisation.", predicate.test(productOwner.getShortName()));
        organisation.getPeople().add(productOwner);
        Assert.assertTrue("Valid Product Owner not recognised as valid.", predicate.test(productOwner.getShortName()));
    }


    @Test
    public void testProjectValidation() {
        BacklogFormViewModel backlogFormViewModel = new BacklogFormViewModel();
        Organisation organisation = new Organisation();
        backlogFormViewModel.setOrganisation(organisation);

        Predicate<String> predicate = backlogFormViewModel.getProjectValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(backlogFormViewModel.projectNameProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));
        Assert.assertFalse("Must not be empty.", predicate.test(""));

        final String projectName = "project shortName";

        Assert.assertFalse("Project must exist.", predicate.test(projectName));

        Project project = new Project(projectName, "longName");

        backlogFormViewModel.projectNameProperty().set(projectName);
        Assert.assertFalse("Project must exist in organisation.", predicate.test(projectName));

        organisation.getProjects().add(project);
        backlogFormViewModel.projectNameProperty().set("");
        backlogFormViewModel.projectNameProperty().set(projectName);
        Assert.assertTrue("Valid project not recognised as valid.", predicate.test(projectName));
    }

    @Test
    public void testScaleValidation() {

    }

}
