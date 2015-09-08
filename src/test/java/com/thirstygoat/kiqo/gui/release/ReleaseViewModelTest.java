package com.thirstygoat.kiqo.gui.release;

import java.time.LocalDate;

import org.junit.*;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

public class ReleaseViewModelTest {
    ReleaseViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new ReleaseViewModel();
        viewModel.load(null, new Organisation());
    }

    @Test
    public void testShortNameValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                viewModel.shortNameValidation().validProperty().get());
        
        viewModel.shortNameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("This name is longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.");
        Assert.assertFalse("Must not be longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.",
                viewModel.shortNameValidation().validProperty().get());

        // validating uniqueness within project requires model data
        final String SHARED_NAME = "not unique";
        final Project project = new Project("myProject", "release names are only unique within projects");
        Release secondRelease = new Release(SHARED_NAME, project, LocalDate.now(), "arbitrary description");
        project.observableReleases().add(secondRelease);
        viewModel.projectProperty().set(project);
        
        viewModel.shortNameProperty().set("unique");
        Assert.assertTrue("Unique name must be valid.",
                viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set(SHARED_NAME);
        Assert.assertEquals(viewModel.projectProperty().get(), secondRelease.projectProperty().get());
        Assert.assertEquals(viewModel.shortNameProperty().get(), secondRelease.shortNameProperty().get());
        Assert.assertFalse("Non-unique name must be invalid.",
                viewModel.shortNameValidation().validProperty().get());
    }

    @Test
    public void testDescriptionValidation() {
        Assert.assertTrue("Must be valid initially.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());
    }

    @Test
    public void testProjectValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.projectValidation().validProperty().get());
        
        viewModel.projectProperty().set(new Project("short", "long"));
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.projectValidation().validProperty().get());
        
        viewModel.projectProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.shortNameValidation().validProperty().get());
    }

    @Test
    public void testDateValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.dateValidation().validProperty().get());
        
        viewModel.dateProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.dateValidation().validProperty().get());
        
        viewModel.dateProperty().set(LocalDate.now());
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.dateValidation().validProperty().get());
        
        // sprint logic needs model info
        Release release = new Release();
        Sprint sprint = new Sprint();
        sprint.setEndDate(LocalDate.of(1, 1, 2));
        release.getSprints().add(sprint);
        viewModel.load(release, new Organisation());
        
        viewModel.dateProperty().set(LocalDate.of(1, 1, 1));
        Assert.assertFalse("Must not be after constituent sprint's endDate.",
                viewModel.dateValidation().validProperty().get());

        viewModel.dateProperty().set(LocalDate.of(1, 1, 3));
        Assert.assertTrue("Valid input not recognised as valid with constituent sprint.",
                viewModel.dateValidation().validProperty().get());
    }

    @Test
    public void testAllValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.allValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertFalse("Must not be valid with partial validity.",
                viewModel.allValidation().validProperty().get());

        viewModel.shortNameProperty().set("Name");
        viewModel.projectProperty().set(new Project());
        viewModel.dateProperty().set(LocalDate.now());
        Assert.assertTrue("Must be valid when all contributors are valid.",
                viewModel.allValidation().validProperty().get());
    }

}
