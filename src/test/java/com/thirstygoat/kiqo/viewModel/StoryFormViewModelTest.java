package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class StoryFormViewModelTest {

    @Test
    public void testShortNameValidation() throws NoSuchFieldException, IllegalAccessException {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<String> predicate = storyFormViewModel.getShortNameValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.shortNameProperty().get()));
        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
        Assert.assertFalse("Must not be longer than 20 characters.", predicate.test("This name is longer than 20 characters."));

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

        Assert.assertTrue("Unique short name not recognised as valid.", predicate.test("unique"));
        Assert.assertFalse("Must be unique within project.", predicate.test(storyName));
    }
    
    @Test
    public void testLongNameValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<String> predicate = storyFormViewModel.getLongNameValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.longNameProperty().get()));
        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
    }

    @Test
    public void testDescriptionValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<String> predicate = storyFormViewModel.getDescriptionValidation();

        Assert.assertTrue("Description should be valid by default.", predicate.test(storyFormViewModel.descriptionProperty().get()));
        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
        Assert.assertTrue("Empty string not recognised as valid.", predicate.test(""));
    }

    @Test
    public void testCreatorValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Organisation organisation = new Organisation();
        storyFormViewModel.setOrganisation(organisation);
        
        Predicate<String> predicate = storyFormViewModel.getCreatorValidation();
        
        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.creatorNameProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));

        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Assert.assertFalse("Creator must exist in organisation.", predicate.test(creator.getShortName()));
        organisation.getPeople().add(creator);
        Assert.assertTrue("Valid creator not recognised as valid.", predicate.test(creator.getShortName()));
    }

    @Test
    public void testProjectValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Organisation organisation = new Organisation();
        storyFormViewModel.setOrganisation(organisation);
        
        Predicate<String> predicate = storyFormViewModel.getProjectValidation();
        
        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.projectNameProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));
        Assert.assertFalse("Must not be empty.", predicate.test(""));
        
        final String projectName = "project shortName";
        
        Assert.assertFalse("Project must exist.", predicate.test(projectName));
        
        Project project = new Project(projectName, "longName");
        // must set projectNameProperty so that projectProperty gets set.
        storyFormViewModel.projectNameProperty().set(projectName);
        Assert.assertFalse("Project must exist in organisation.", predicate.test(projectName));
        
        organisation.getProjects().add(project);
        storyFormViewModel.projectNameProperty().set("");
        storyFormViewModel.projectNameProperty().set(projectName);
        Assert.assertTrue("Valid project not recognised as valid.", predicate.test(projectName));
    }

    @Test
    public void testBacklogValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<String> predicate = storyFormViewModel.getBacklogValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.backlogNameProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));

        // Setup objects for testing cases in which backlog belongs to project and does not belong to a project.
        final String projectName = "project shortName";
        
        Organisation organisation = new Organisation();
        Project project = new Project(projectName, "longName");
        Person productOwner = new Person("person PO", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
        Backlog backlog1 = new Backlog("backlog in the same project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
        Backlog backlog2 = new Backlog("backlog not in project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
        project.setBacklogs(Arrays.asList(backlog1));
        
        organisation.getProjects().add(project);
        storyFormViewModel.setOrganisation(organisation);
        
        storyFormViewModel.projectNameProperty().set(projectName);

        //TODO FIX THIS TEST
        // Backlog belongs to selected project
        Assert.assertTrue("Valid backlog should be recognised as valid.", predicate.test(backlog1.getShortName()));

        // Backlog does not belong to selected project
        Assert.assertFalse("Backlog must belong to selected project.", predicate.test(backlog2.getShortName()));
    }

    @Test
    public void testPriorityValidation() throws NoSuchFieldException, IllegalAccessException {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<String> predicate = storyFormViewModel.getPriorityValidation();

        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.priorityProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));

        Assert.assertFalse("Value must be higher than story.MIN_PRIORITY", predicate.test(Integer.toString(Story.MIN_PRIORITY - 1)));
        Assert.assertFalse("Value must be smaller than story.MAX_PRIORITY", predicate.test(Integer.toString(Story.MAX_PRIORITY + 1)));
    }

    @Test
    public void testCreatorEditability() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project("shortName", "longName");
        Story story = new Story("shortName", "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);

        Assert.assertTrue("Creator field should be editable by default.", storyFormViewModel.getCreatorEditable().get());

        storyFormViewModel.setStory(null);
        Assert.assertTrue("Creator field should be editable for a new story.", storyFormViewModel.getCreatorEditable().get());

        // TODO FIX NEXT TWO LINES
//        storyFormViewModel.setStory(story);
        Assert.assertFalse("Creator field should be not be editable for an existing story.", storyFormViewModel.getCreatorEditable().get());
    }

    @Test
    public void testScaleValidation() {
        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
        Predicate<Scale> predicate = storyFormViewModel.getScaleValidation();

        Scale workingScale = Scale.FIBONACCI;

        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.scaleProperty().get()));
        Assert.assertFalse("Must not be null.", predicate.test(null));

        storyFormViewModel.scaleProperty().set(workingScale);
        Assert.assertTrue("Valid scale not recognised as valid.", predicate.test(workingScale));
    }
}
