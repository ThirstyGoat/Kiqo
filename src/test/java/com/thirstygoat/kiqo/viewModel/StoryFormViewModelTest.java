package com.thirstygoat.kiqo.viewModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import com.thirstygoat.kiqo.model.*;

import org.junit.Assert;
import org.junit.Test;

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
        Story story = new Story(storyName, "longName", "description", creator, project, null, 0, Scale.FIBONACCI, 0);
        project.setUnallocatedStories(new ArrayList<Story>(Arrays.asList(story)));
        
        Organisation organisation = new Organisation();
        organisation.getPeople().add(creator);
        organisation.getProjects().add(project);
        storyFormViewModel.setOrganisation(organisation);
        
        // workaround so that project field gets set
        storyFormViewModel.projectNameProperty().set(projectName);
        storyFormViewModel.getProjectValidation().test(storyFormViewModel.projectNameProperty().get());
        
        // Because otherwise the project is null so will always return false
//        Field privateProject = storyFormViewModel.getClass().getDeclaredField("project");
//        privateProject.setAccessible(true);
//        privateProject.set(storyFormViewModel, project);

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

//    @Test
//    public void testCreatorValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getCreatorValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.creatorProperty().get()));
//        Assert.assertFalse("Must not be null.", predicate.test(null));
//
//        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
//        Assert.assertTrue("Valid creator not recognised as valid.", predicate.test(creator.getShortName()));
//    }
//
//    @Test
//    public void testProjectValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getProjectValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testBacklogValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getBacklogValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyFormViewModel.backlogProperty().get()));
//        Assert.assertFalse("Must not be null.", predicate.test(null));
//
//        // Setup objects for testing cases in which backlog belongs to project and does not belong to a project.
//        Organisation organisation = new Organisation();
//        Project project = new Project("project shortName", "longName");
//        Person productOwner = new Person("person PO", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
//        Backlog backlog1 = new Backlog("backlog in the same project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
//        Backlog backlog2 = new Backlog("backlog not in project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
//        project.setBacklogs(Arrays.asList(backlog1));
//        storyFormViewModel.projectProperty().set(project); // TODO requires "protected ObjectProperty<Project> projectProperty"
//
//        // Backlog belongs to selected project
//        Assert.assertTrue("Valid backlog should be recognised as valid.", predicate.test(backlog1.getShortName()));
//
//        // Backlog does not belong to selected project
//        Assert.assertFalse("Backlog must belong to selected project.", predicate.test(backlog2.getShortName()));
//    }
//
//    @Test
//    public void testPriorityValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getPriorityValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testAcceptanceCriteriaValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getAcceptanceCriteriaValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testScaleValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getScaleValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEstimateValidation() {
//        StoryFormViewModel storyFormViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyFormViewModel.getEstimateValidation();
//
//        Assert.fail("Not yet implemented");
//    }
}
