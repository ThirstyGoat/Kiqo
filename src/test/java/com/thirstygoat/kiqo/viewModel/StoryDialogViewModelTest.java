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
        Assert.assertFalse("Must not be null.", storyDialogViewModel.backlogIsValid());
    }

}
