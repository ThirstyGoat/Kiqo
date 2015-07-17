//package com.thirstygoat.kiqo.viewModel;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.function.Predicate;
//
//import com.thirstygoat.kiqo.model.*;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//public class StoryFormViewModelTest {
//
//    @Test
//    public void testShortNameValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getShortNameValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyDialogViewModel.shortNameProperty().get()));
//        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
//        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
//        Assert.assertFalse("Must not be longer than 20 characters.", predicate.test("This name is longer than 20 characters."));
//
//        // uniqueness within project
//        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
//        Project project = new Project("project shortName", "longName");
//        final String sharedShortName = "story shortName";
//        Story story = new Story(sharedShortName, "longName", "description", creator, project, null, 0, 0, Scale.FIBONACCI);
//        project.setUnallocatedStories(Arrays.asList(story));
//        Assert.assertTrue("Unique short name not recognised as valid.", predicate.test("story shortName unique"));
//        Assert.assertFalse("Must be unique within project.", predicate.test(sharedShortName));
//    }
//
//    @Test
//    public void testLongNameValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getLongNameValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyDialogViewModel.longNameProperty().get()));
//        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
//        Assert.assertFalse("Must not be an empty string.", predicate.test(""));
//    }
//
//    @Test
//    public void testDescriptionValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getDescriptionValidation();
//
//        Assert.assertTrue("Description should be valid by default.", predicate.test(storyDialogViewModel.descriptionProperty().get()));
//        Assert.assertTrue("Valid input not recognised as valid.", predicate.test("Billy Goat"));
//        Assert.assertTrue("Empty string not recognised as valid.", predicate.test(""));
//    }
//
//    @Test
//    public void testCreatorValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getPersonValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyDialogViewModel.creatorProperty().get()));
//        Assert.assertFalse("Must not be null.", predicate.test(null));
//
//        Person creator = new Person("person shortName", "longName", "description", "userId", "email", "phone", "dept", new ArrayList<Skill>());
//        Assert.assertTrue("Valid creator not recognised as valid.", predicate.test(creator.getShortName()));
//    }
//
//    @Test
//    public void testProjectValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getProjectValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testBacklogValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getBacklogValidation();
//
//        Assert.assertFalse("Must not be valid initially.", predicate.test(storyDialogViewModel.backlogProperty().get()));
//        Assert.assertFalse("Must not be null.", predicate.test(null));
//
//        // Setup objects for testing cases in which backlog belongs to project and does not belong to a project.
//        Organisation organisation = new Organisation();
//        Project project = new Project("project shortName", "longName");
//        Person productOwner = new Person("person PO", "longName", "description", "userId", "email", "phone", "dept", Arrays.asList(organisation.getPoSkill()));
//        Backlog backlog1 = new Backlog("backlog in the same project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
//        Backlog backlog2 = new Backlog("backlog not in project", "longName", "description", productOwner, project, new ArrayList<>(), Scale.FIBONACCI);
//        project.setBacklogs(Arrays.asList(backlog1));
//        storyDialogViewModel.projectProperty().set(project); // TODO requires "protected ObjectProperty<Project> projectProperty"
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
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getPriorityValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testAcceptanceCriteriaValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getAcceptanceCriteriaValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testScaleValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getScaleValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//
//    @Test
//    public void testEstimateValidation() {
//        StoryFormViewModel storyDialogViewModel = new StoryFormViewModel();
//        Predicate<String> predicate = storyDialogViewModel.getEstimateValidation();
//
//        Assert.fail("Not yet implemented");
//    }
//}
