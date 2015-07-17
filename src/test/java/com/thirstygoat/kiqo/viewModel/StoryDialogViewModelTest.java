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
        
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid(), "Must not be valid initially.");
        
        // form
        storyDialogViewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue(storyDialogViewModel.shortNameIsValid(), "Valid input not recognised as valid.");

        storyDialogViewModel.shortNameProperty().set("");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid(), "Must not be an empty string.");

        storyDialogViewModel.shortNameProperty().set("This name is longer than 20 characters.");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid(), "Must not be longer than 20 characters.");
        
        // uniqueness within project
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project("project shortName", "longName");
        Story story = new Story("story shortName", "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);

        storyDialogViewModel.shortNameProperty().set("story shortName unique");
        Assert.assertTrue(storyDialogViewModel.shortNameIsValid(), "Unique short name not recognised as valid.");
        storyDialogViewModel.shortNameProperty().set("story shortName");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid(), "Must be unique within project.");
    }
    
    @Test
    public void testLongNameValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertFalse(storyDialogViewModel.longNameIsValid(), "Must not be valid initially.");
        
        storyDialogViewModel.longNameProperty().set("Billy Goat");
        Assert.assertTrue(storyDialogViewModel.longNameIsValid(), "Valid input not recognised as valid.");

        storyDialogViewModel.longNameProperty().set("");
        Assert.assertFalse(storyDialogViewModel.longNameIsValid(), "Must not be an empty string.");
    }

    @Test
    public void testCreatorValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();

        Assert.assertFalse(storyDialogViewModel.creatorIsValid(), "Must not be valid initially.");

        storyDialogViewModel.creatorProperty().set(null);
        Assert.assertFalse(storyDialogViewModel.creatorIsValid(), "Must not be null.");

        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        storyDialogViewModel.creatorProperty().set(creator);
        Assert.assertTrue(storyDialogViewModel.creatorIsValid(), "Valid creator not recognised as valid.");
    }

    @Test
    public void testBacklogValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();

        Assert.assertFalse(storyDialogViewModel.backlogIsValid(), "Must not be valid initially.");

        storyDialogViewModel.backlogProperty().set(null);
        Assert.assertFalse(storyDialogViewModel.backlogIsValid(), "Must not be null.");
    }

}
