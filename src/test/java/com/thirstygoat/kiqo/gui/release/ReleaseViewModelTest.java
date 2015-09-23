package com.thirstygoat.kiqo.gui.release;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateReleaseCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ReleaseViewModelTest {
    private ReleaseViewModel viewModel;
    private Organisation organisation;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Story unreadyStory;
    private Story readyStory;
    private Sprint sprint;
    private Person po;
    private Team team;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        viewModel = new ReleaseViewModel();
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
        backlog.observableStories().add(readyStory);
        backlog.observableStories().add(unreadyStory);
        sprint = new Sprint("sprintGoal", "sprintLongName", "sprintDescription", backlog, release, team,
                        LocalDate.now().minusDays(11), LocalDate.now().minusDays(5), new ArrayList<>());
    }

    @Test
    public void testShortNameValidation() {
        viewModel.load(null, organisation);

        Assert.assertFalse("Must not be valid initially.",
                        viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                        viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.", viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                        viewModel.shortNameValidation().validProperty().get());

        viewModel.shortNameProperty().set(
                        "This name is longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.");
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
        viewModel.load(null, organisation);

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
        viewModel.load(null, organisation);

        Assert.assertFalse("Must not be valid initially.", viewModel.projectValidation().validProperty().get());

        viewModel.projectProperty().set(new Project("short", "long"));
        Assert.assertTrue("Valid input not recognised as valid.",
                        viewModel.projectValidation().validProperty().get());

        viewModel.projectProperty().set(null);
        Assert.assertFalse("Must not be null.", viewModel.shortNameValidation().validProperty().get());
    }

    @Test
    public void testDateValidation() {
        viewModel.load(null, organisation);

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

    /**
     * Populate a ReleaseViewModel's fields with valid data.
     * @param viewModel
     */
    public void populateFields(ReleaseViewModel viewModel) {
        viewModel.shortNameProperty().set("aReleaseShortName");
        viewModel.dateProperty().set(LocalDate.now());
        viewModel.projectProperty().set(project);
    }

    @Test
    public void testAllValidation() {
        viewModel.load(null, organisation);

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

    @Test
    public void newRelease_doNothingTest() {
    }

    @Test
    public void newRelease_validFieldsTest() {
        viewModel.load(null, organisation);
        populateFields(viewModel);

        Assert.assertEquals(CreateReleaseCommand.class, viewModel.getCommand().getClass());

        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        UndoManager.getUndoManager().undoCommand();
        UndoManager.getUndoManager().redoCommand();

        Assert.assertEquals(true, organisation.getProjects().stream().flatMap(project -> project.getReleases().stream())
                        .filter(release -> release.getShortName().equals("aReleaseShortName")).findAny().isPresent());
    }

    @Test
    public void editRelease_changeProjectTest() {
        viewModel.load(release, organisation);
        Assert.assertEquals(project, release.getProject());
        Assert.assertTrue(project.getReleases().contains(release));

        Project anotherProject = new Project();
        viewModel.projectProperty().set(anotherProject);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        Assert.assertEquals(anotherProject, release.getProject());
        Assert.assertEquals(anotherProject, viewModel.projectProperty().get());
        Assert.assertTrue(anotherProject.getReleases().contains(release));
        Assert.assertFalse(project.getReleases().contains(release));

        UndoManager.getUndoManager().undoCommand();
        Assert.assertEquals(project, release.getProject());
        Assert.assertEquals(project, viewModel.projectProperty().get());
        Assert.assertTrue(project.getReleases().contains(release));
    }


}
