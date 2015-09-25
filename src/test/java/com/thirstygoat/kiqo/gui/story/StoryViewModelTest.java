package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.viewModel.StoryViewModel;
import com.thirstygoat.kiqo.model.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class StoryViewModelTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    private StoryViewModel viewModel;
    private Organisation organisation;
    private Story story1;
    private Story story2;
    private Project project;
    private Release release;
    private Backlog backlog;
    private Person person;
    private Team team;
    private Skill skill1;
    private Skill skill2;

    @Before
    public void setup() {
        viewModel = new StoryViewModel();
        organisation = new Organisation();
        project = new Project("projectShortName", "projectLongName");
        person = new Person("existingPerson", "", "", "", "", "", "", Arrays.asList());
        backlog = new Backlog("backlog", "blLongName", "blDescription", person, project, new ArrayList<>(), Scale.FIBONACCI);
        project.observableBacklogs().add(backlog);
        release = new Release("releaseShortName", project, LocalDate.now(), "releaseDescription");
        team = new Team("teamShortName", "teamDescriptoin", new ArrayList<>());
        organisation.getProjects().add(project);
        organisation.getTeams().add(team);
        organisation.getPeople().add(person);
        project.observableReleases().add(release);
        story1 = new Story("story1", "longName", "", person, project, backlog, 666, Scale.FIBONACCI, 333, false, false, null);
        story2 = new Story("story2", "longName", "", person, project, backlog, 420, Scale.FIBONACCI, 42, true, false, null);
        project.getUnallocatedStories().add(story1);
        project.getUnallocatedStories().add(story2);
        skill1 = new Skill("skill1", "");
        skill2 = new Skill("skill2", "");
        organisation.getSkills().add(skill1);
        organisation.getSkills().add(skill2);
    }

    @Test
    public void testShortNameValidation() throws NoSuchFieldException, IllegalAccessException {
        StoryViewModel storyViewModel = new StoryViewModel();

        Assert.assertFalse("Must not be valid initially.",
                storyViewModel.shortNameValidation().validProperty().get());

        storyViewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyViewModel.shortNameValidation().validProperty().get());

        storyViewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                storyViewModel.shortNameValidation().validProperty().get());

        storyViewModel.shortNameProperty().set("This name is longer than 20 characters.");
        Assert.assertFalse("Must not be longer than 20 characters.",
                        storyViewModel.shortNameValidation().validProperty().get());

        // validating uniqueness within project requires model data
        final String projectName = "project shortName";
        final String storyName = "story shortName";

        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project(projectName, "longName");
        Story story = new Story(storyName, "longName", "description", creator, project, null, 0, Scale.FIBONACCI, 0, false, false, null);
        project.setUnallocatedStories(new ArrayList<>(Arrays.asList(story)));

        Organisation organisation = new Organisation();
        organisation.getPeople().add(creator);
        organisation.getProjects().add(project);
        storyViewModel.organisationProperty().setValue(organisation);

        // set project field
        storyViewModel.projectProperty().set(project);

        storyViewModel.shortNameProperty().set("unique");
        Assert.assertTrue("Unique short name not recognised as valid.",
                storyViewModel.shortNameValidation().validProperty().get());

        storyViewModel.shortNameProperty().set(storyName);
        Assert.assertFalse("Must be unique within project.", storyViewModel.shortNameValidation().validProperty().get());
    }

    @Test
    public void testLongNameValidation() {
        StoryViewModel storyViewModel = new StoryViewModel();

        Assert.assertFalse("Must not be valid initially.",
                storyViewModel.longNameValidation().validProperty().get());

        storyViewModel.longNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyViewModel.longNameValidation().validProperty().get());

        storyViewModel.longNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                storyViewModel.longNameValidation().validProperty().get());
    }

    @Test
    public void testDescriptionValidation() {
        StoryViewModel storyViewModel = new StoryViewModel();

        Assert.assertTrue("Description should be valid by default.",
                        storyViewModel.descriptionValidation().validProperty().get());

        storyViewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyViewModel.descriptionValidation().validProperty().get());

        storyViewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                        storyViewModel.descriptionValidation().validProperty().get());
    }

    @Test
    public void testCreatorValidation() {
        StoryViewModel storyViewModel = new StoryViewModel();
        Organisation organisation = new Organisation();
        storyViewModel.organisationProperty().set(organisation);

        Assert.assertFalse("Must not be valid initially.",
                storyViewModel.creatorValidation().validProperty().get());

        storyViewModel.creatorProperty().set(null);
        Assert.assertFalse("Must not be null.", storyViewModel.creatorValidation().validProperty().get());

        Person creator1 = new Person("person1 shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Person creator2 = new Person("person2 shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        organisation.getPeople().add(creator2);

        storyViewModel.creatorProperty().set(creator1);
        Assert.assertFalse("Creator must exist in organisation.",
                        storyViewModel.creatorValidation().validProperty().get());
    }

    @Test
    public void testProjectValidation() {
        StoryViewModel storyViewModel = new StoryViewModel();
        Organisation organisation = new Organisation();
        storyViewModel.organisationProperty().set(organisation);

        Assert.assertFalse("Must not be valid initially.", storyViewModel.projectValidation().validProperty().get());

        storyViewModel.projectProperty().set(null);
        Assert.assertFalse("Must not be null.",
                storyViewModel.projectValidation().validProperty().get());
    }

    @Test
    public void testBacklogValidation() {
        StoryViewModel storyViewModel = new StoryViewModel();

        storyViewModel.priorityProperty().set(Story.MIN_PRIORITY - 1);
        Assert.assertFalse("Value must be higher than story.MIN_PRIORITY",
                storyViewModel.priorityValidation().validProperty().get());

        storyViewModel.priorityProperty().set(Story.MAX_PRIORITY + 1);
        Assert.assertFalse("Value must be smaller than story.MAX_PRIORITY",
                storyViewModel.priorityValidation().validProperty().get());
    }

    @Test
    public void testCreatorEditability() {
        Organisation organisation = new Organisation();
        StoryViewModel storyViewModel = new StoryViewModel();
        storyViewModel.organisationProperty().set(organisation);
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<>());
        organisation.getPeople().add(creator);
        Project project = new Project("shortName", "longName");
        Story story = new Story("shortName", "longName", "description", creator, project, null, 0, Scale.FIBONACCI, 0, false, false, null);

        Assert.assertFalse("Creator field should be editable by default.", storyViewModel.getCreatorEditable().get());

        storyViewModel.load(story, organisation);
        Assert.assertFalse("Creator field should be editable for a new story.",
                        storyViewModel.getCreatorEditable().get());
    }

    @Test
    public void testDependencyList() {
        // Add some stories to the backlog that story1 is in
        Story a, b, c, d;
        story1.getBacklog().observableStories().addAll(Arrays.asList(
                        a = new Story("A", "", "", person, project, backlog, 0, Scale.FIBONACCI, 0, false, false, null),
                        b = new Story("B", "", "", person, project, backlog, 0, Scale.FIBONACCI, 0, false, false, null)));

        Backlog backlog2 = new Backlog();
        backlog2.observableStories().addAll(Arrays.asList(
                        c = new Story("C", "", "", person, project, backlog2, 0, Scale.FIBONACCI, 0, false, false, null),
                        d = new Story("D", "", "", person, project, backlog2, 0, Scale.FIBONACCI, 0, false, false, null)));

        viewModel.load(story1, organisation);

        ListProperty<Story> eligibleDependencies = new SimpleListProperty<>();
        
        // Check that only stories in the same backlog are available as dependencies
        eligibleDependencies.bind(viewModel.eligibleDependencies());
		Assert.assertEquals(true, eligibleDependencies.get().containsAll(Arrays.asList(a, b)));
        Assert.assertEquals(false, eligibleDependencies.get().containsAll(Arrays.asList(c, d)));

        // Check that eligible stories change when backlog is not set
        viewModel.backlogProperty().set(null);
        Assert.assertEquals(Collections.emptyList(), eligibleDependencies.get());

        // Check that eligible stories change when a different backlog is set
        viewModel.backlogProperty().set(backlog2);
        Assert.assertEquals(true, eligibleDependencies.get().containsAll(Arrays.asList(c, d)));
        Assert.assertEquals(false, eligibleDependencies.get().containsAll(Arrays.asList(a, b)));
    }

    @Test
    public void testDependencyChanges() {
        viewModel.load(story1, organisation);
        Assert.assertEquals(false, viewModel.dependenciesProperty().contains(story2));

        // Add story to dependencies vie ViewModel
        viewModel.dependenciesProperty().add(story2);
        UndoManager.getUndoManager().doCommand(viewModel.getCommand());
        Assert.assertEquals(true, story1.getDependencies().contains(story2));

        // Remove dependency using undo command. Should update both the Model and ViewModel
        UndoManager.getUndoManager().undoCommand();
        Assert.assertEquals(false, story1.getDependencies().contains(story2));
        Assert.assertEquals(false, viewModel.dependenciesProperty().contains(story2));

        // Reinstate skill using redo command. Should update both the
        UndoManager.getUndoManager().redoCommand();
        Assert.assertEquals(true, story1.getDependencies().contains(story2));
        Assert.assertEquals(true, viewModel.dependenciesProperty().contains(story2));
    }
}
