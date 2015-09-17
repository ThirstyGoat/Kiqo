package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.gui.viewModel.StoryViewModel;
import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;


public class StoryViewModelTest {

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
        Story story = new Story(storyName, "longName", "description", creator, project, null, 0, Scale.FIBONACCI, 0, false, false);
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
        Assert.assertFalse("Must be unique within project.",
                storyViewModel.shortNameValidation().validProperty().get());
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
        Assert.assertFalse("Must not be null.",
                storyViewModel.creatorValidation().validProperty().get());

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

        Assert.assertFalse("Must not be valid initially.",
                storyViewModel.projectValidation().validProperty().get());

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
        Story story = new Story("shortName", "longName", "description", creator, project, null, 0, Scale.FIBONACCI, 0, false, false);

        Assert.assertTrue("Creator field should be editable by default.", storyViewModel.getCreatorEditable().get());

        storyViewModel.load(story, organisation);
        Assert.assertTrue("Creator field should be editable for a new story.", storyViewModel.getCreatorEditable().get());
    }
}
