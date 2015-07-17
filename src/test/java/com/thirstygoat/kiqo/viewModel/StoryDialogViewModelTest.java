package com.thirstygoat.kiqo.viewModel;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Skill;
import com.thirstygoat.kiqo.model.Story;

public class StoryDialogViewModelTest {

    @Test
    public void testShortNameValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid());
        
        // form
        storyDialogViewModel.shortNameProperty().set("Billy Goat");
        Assert.assertTrue(storyDialogViewModel.shortNameIsValid());

        storyDialogViewModel.shortNameProperty().set("");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid());

        storyDialogViewModel.shortNameProperty().set("This name is longer than 20 characters.");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid());
        
        // uniqueness within project
        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
        Project project = new Project("project shortName", "longName");
        Story story = new Story("story shortName", "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);

        storyDialogViewModel.shortNameProperty().set("story shortName unique");
        Assert.assertTrue(storyDialogViewModel.shortNameIsValid());
        storyDialogViewModel.shortNameProperty().set("story shortName");
        Assert.assertFalse(storyDialogViewModel.shortNameIsValid());
    }
    
    @Test
    public void testLongNameValidation() {
        StoryDialogViewModel storyDialogViewModel = new StoryDialogViewModel();
        
        Assert.assertFalse(storyDialogViewModel.longNameIsValid());
        
        storyDialogViewModel.longNameProperty().set("Billy Goat");
        Assert.assertTrue(storyDialogViewModel.longNameIsValid());

        storyDialogViewModel.longNameProperty().set("");
        Assert.assertFalse(storyDialogViewModel.longNameIsValid());
    }

}
