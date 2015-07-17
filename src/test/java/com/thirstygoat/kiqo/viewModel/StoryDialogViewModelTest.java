package com.thirstygoat.kiqo.viewModel;

import java.util.ArrayList;
import java.util.Arrays;

import com.thirstygoat.kiqo.model.*;
import org.junit.Assert;
import org.junit.Test;

public class StoryDialogViewModelTest {

    @Test
    public void testShortNameValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertFalse("Must not be valid initially.", storyDialogViewModel.shortNameIsValid());
        
        // form
        storyDialogViewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",storyDialogViewModel.shortNameIsValid());

        storyDialogViewModel.shortNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.", storyDialogViewModel.shortNameIsValid());

        storyDialogViewModel.shortNameProperty().set("This name is longer than 20 characters.");
        Assert.assertFalse("Must not be longer than 20 characters.", storyDialogViewModel.shortNameIsValid());
        
        // uniqueness within project
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project("project shortName", "longName");
        Story story = new Story("story shortName", "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);
        project.setUnallocatedStories(Arrays.asList(story));

        storyDialogViewModel.shortNameProperty().set("story shortName unique");
        Assert.assertTrue("Unique short name not recognised as valid.", storyDialogViewModel.shortNameIsValid());
        storyDialogViewModel.shortNameProperty().set("story shortName");
        Assert.assertFalse("Must be unique within project.", storyDialogViewModel.shortNameIsValid());
    }
    
    @Test
    public void testLongNameValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertFalse("Must not be valid initially.", storyDialogViewModel.longNameIsValid());
        
        storyDialogViewModel.longNameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.", storyDialogViewModel.longNameIsValid());

        storyDialogViewModel.longNameProperty().set("");
        Assert.assertFalse("Must not be an empty string.", storyDialogViewModel.longNameIsValid());
    }
    
    @Test
    public void testDescriptionValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertTrue("Description should be valid by default.", storyDialogViewModel.descriptionIsValid());

        storyDialogViewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue(storyDialogViewModel.descriptionIsValid(), "Valid input not recognised as valid.");

        storyDialogViewModel.descriptionProperty().set("");
        Assert.assertTrue(storyDialogViewModel.descriptionIsValid(), "Empty string not recognised as valid.");
    }

    @Test
    public void testCreatorValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();

        Assert.assertFalse("Must not be valid initially.", storyDialogViewModel.creatorIsValid());

        storyDialogViewModel.creatorProperty().set(null);
        Assert.assertFalse("Must not be null.", storyDialogViewModel.creatorIsValid());

        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        storyDialogViewModel.creatorProperty().set(creator);
        Assert.assertTrue("Valid creator not recognised as valid.", storyDialogViewModel.creatorIsValid());
    }

    @Test
    public void testBacklogValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();

        Assert.assertFalse("Must not be valid initially.", storyDialogViewModel.backlogIsValid());

        storyDialogViewModel.backlogProperty().set(null);
        Assert.assertFalse(storyDialogViewModel.backlogIsValid(), "Must not be null.");
        
        // Setup objects for testing cases in which backlog belongs to project and does not belong to a project.
        Organisation organisation = new Organisation();
        Project project = new Project("project shortName", "longName");
        Person productOwner = new Person("person PO", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
        Backlog backlog1 = new Backlog("backlog in the same project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
        Backlog backlog2 = new Backlog("backlog not in project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
        project.setBacklogs(Arrays.asList(backlog1));
        storyDialogViewModel.projectProperty().set(project);
       
        // Backlog belongs to selected project
        storyDialogViewModel.backlogProperty().set(backlog1);
        Assert.assertTrue("Valid backlog should be recognised as valid.", storyDialogViewModel.backlogIsValid());
        
        // Backlog does not belong to selected project
        storyDialogViewModel.backlogProperty().set(backlog2);
        Assert.assertFalse("Backlog must belong to selected project.", storyDialogViewModel.backlogIsValid());
    }
}
