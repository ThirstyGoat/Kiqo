package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class StoryFormViewModelTest {

    @Test
    public void testShortNameValidation() throws NoSuchFieldException, IllegalAccessException {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();

        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        storyFormViewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        storyFormViewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        storyFormViewModel.shortNameProperty().set("This name is longer than 20 characters.");
        Assert.assertFalse("Must not be longer than 20 characters.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        // validating uniqueness within project requires model data
        final String projectName = "project shortName";
        final String storyName = "story shortName";
        
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project(projectName, "longName");
        Story story = new Story(storyName, "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);
        project.setUnallocatedStories(new ArrayList<Story>(Arrays.asList(story)));
        
        Organisation organisation = new Organisation();
        organisation.getPeople().add(creator);
        organisation.getProjects().add(project);
        storyFormViewModel.setOrganisation(organisation);
        
        // set project field
        storyFormViewModel.projectNameProperty().set(projectName);

        storyFormViewModel.shortNameProperty().set("unique");
        Assert.assertTrue("Unique short name not recognised as valid.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        storyFormViewModel.shortNameProperty().set(storyName);
        Assert.assertFalse("Must be unique within project.",
                storyFormViewModel.shortNameValidation().validProperty().get());
    }
    
    @Test
    public void testLongNameValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();

        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.shortNameValidation().validProperty().get());

        storyFormViewModel.longNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyFormViewModel.longNameValidation().validProperty().get());

        storyFormViewModel.longNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                storyFormViewModel.longNameValidation().validProperty().get());
    }

    @Test
    public void testDescriptionValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();

        Assert.assertTrue("Description should be valid by default.",
                storyFormViewModel.descriptionValidation().validProperty().get());

        storyFormViewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                storyFormViewModel.descriptionValidation().validProperty().get());

        storyFormViewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                storyFormViewModel.descriptionValidation().validProperty().get());
    }

    @Test
    public void testCreatorValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Organisation organisation = new Organisation();
        storyFormViewModel.setOrganisation(organisation);
        
        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.creatorValidation().validProperty().get());

        storyFormViewModel.creatorNameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                storyFormViewModel.creatorValidation().validProperty().get());

        Person creator1 = new Person("person1 shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Person creator2 = new Person("person2 shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        organisation.getPeople().add(creator2);

        storyFormViewModel.creatorNameProperty().set(creator1.getShortName());
        Assert.assertFalse("Creator must exist in organisation.",
                storyFormViewModel.creatorValidation().validProperty().get());

        storyFormViewModel.creatorNameProperty().set(creator2.getShortName());
        Assert.assertTrue("Valid creator not recognised as valid.",
                storyFormViewModel.creatorValidation().validProperty().get());
    }

    @Test
    public void testProjectValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Organisation organisation = new Organisation();
        storyFormViewModel.setOrganisation(organisation);
        
        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.projectValidation().validProperty().get());

        storyFormViewModel.projectProperty().set(null);
        Assert.assertFalse("Must not be null.",
                storyFormViewModel.projectValidation().validProperty().get());

        storyFormViewModel.projectNameProperty().set("");
        Assert.assertFalse("Must not be empty.",
                storyFormViewModel.projectValidation().validProperty().get());
        
        final String projectName = "project shortName";

        storyFormViewModel.projectNameProperty().set(projectName);
        Assert.assertFalse("Project must exist.",
                storyFormViewModel.projectValidation().validProperty().get());
        
        Project project = new Project(projectName, "longName");
        // must set projectNameProperty so that projectProperty gets set.
        storyFormViewModel.projectNameProperty().set(projectName);
        Assert.assertFalse("Project must exist in organisation.",
                storyFormViewModel.projectValidation().validProperty().get());
        
        organisation.getProjects().add(project);
        storyFormViewModel.projectNameProperty().set("");
        storyFormViewModel.projectNameProperty().set(projectName);
        Assert.assertTrue("Valid project not recognised as valid.",
                storyFormViewModel.projectValidation().validProperty().get());
    }

    @Test
    public void testPriorityValidation() throws NoSuchFieldException, IllegalAccessException {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();

        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.priorityValidation().validProperty().get());

        storyFormViewModel.priorityProperty().set(null);
        Assert.assertFalse("Must not be null.",
                storyFormViewModel.priorityValidation().validProperty().get());

        storyFormViewModel.priorityProperty().set(Integer.toString(Story.MIN_PRIORITY - 1));
        Assert.assertFalse("Value must be higher than story.MIN_PRIORITY",
                storyFormViewModel.priorityValidation().validProperty().get());

        storyFormViewModel.priorityProperty().set(Integer.toString(Story.MAX_PRIORITY + 1));
        Assert.assertFalse("Value must be smaller than story.MAX_PRIORITY",
                storyFormViewModel.priorityValidation().validProperty().get());
    }

    @Test
    public void testCreatorEditability() {
        Organisation organisation = new Organisation();
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        storyFormViewModel.setOrganisation(organisation);
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<>());
        organisation.getPeople().add(creator);
        Project project = new Project("shortName", "longName");
        Story story = new Story("shortName", "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);

        Assert.assertTrue("Creator field should be editable by default.", storyFormViewModel.getCreatorEditable().get());

        storyFormViewModel.setStory(null);
        Assert.assertTrue("Creator field should be editable for a new story.", storyFormViewModel.getCreatorEditable().get());

        storyFormViewModel.setStory(story);
        Assert.assertFalse("Creator field should be not be editable for an existing story.", storyFormViewModel.getCreatorEditable().get());
    }

    @Test
    public void testScaleValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();

        Scale workingScale = Scale.FIBONACCI;

        Assert.assertFalse("Must not be valid initially.",
                storyFormViewModel.scaleValidation().validProperty().get());

        storyFormViewModel.scaleProperty().set(null);
        Assert.assertFalse("Must not be null.",
                storyFormViewModel.scaleValidation().validProperty().get());

        storyFormViewModel.scaleProperty().set(workingScale);
        Assert.assertTrue("Valid scale not recognised as valid.",
                storyFormViewModel.scaleValidation().validProperty().get());
    }
}
