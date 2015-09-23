package com.thirstygoat.kiqo.gui.skill;

import org.junit.*;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.create.CreateSkillCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

public class SkillViewModelTest {
    SkillViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        viewModel = new SkillViewModel();
        viewModel.load(null, new Organisation());
    }
    
    @Test
    public final void testCreateCommand_new() {
        Command command;
        
        command = viewModel.getCommand();
        Assert.assertFalse(viewModel.nameValidation().isValid()); // just to be sure
        Assert.assertNotNull("command must be generated (even when validation is not passing)", command);
        Assert.assertEquals("command must be a CreateSkillCommand", CreateSkillCommand.class, command.getClass());
    }

    @Test
    public final void testCreateCommand_edit() {
        // set up an existing skill to be edited
        Skill skill = new Skill("name", "description");
        Organisation organisation = new Organisation();
        organisation.getSkills().add(skill);
        viewModel.load(skill, organisation);
        
        Command command;
        
        // no changes
        command = viewModel.getCommand();
        Assert.assertNull("command must be null if no changes made", command);

        // blue skies
        viewModel.nameProperty().set("Valid name");
        command = viewModel.getCommand();
        Assert.assertEquals("command must include all changes (1)", "1 change", command.toString());
        
        // failing validation
        viewModel.nameProperty().set("Invalid name because it is too long");
        command = viewModel.getCommand();
        Assert.assertFalse(viewModel.nameValidation().isValid()); // just to be sure
        Assert.assertEquals("command must include all changes (1) even when validation is not passing", "1 change", command.toString());
        
        // multiple changes
        viewModel.nameProperty().set("New name");
        viewModel.descriptionProperty().set("New description");
        command = viewModel.getCommand();
        Assert.assertEquals("command must include all changes (2)", "2 changes", command.toString());
    }

    @Test
    public final void testNameValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("");
        Assert.assertFalse("Must not be an empty string.",
                viewModel.nameValidation().validProperty().get());
        
        viewModel.nameProperty().set(null);
        Assert.assertFalse("Must not be null.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set("This name is longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.");
        Assert.assertFalse("Must not be longer than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters.",
                viewModel.nameValidation().validProperty().get());

        // validating uniqueness within project requires model data
        final String SHARED_NAME = "not unique";
        Skill secondSkill = new Skill(SHARED_NAME, "arbitrary description");
        viewModel.organisationProperty().get().getSkills().add(secondSkill);
        
        viewModel.nameProperty().set("unique");
        Assert.assertTrue("Unique name must be valid.",
                viewModel.nameValidation().validProperty().get());

        viewModel.nameProperty().set(SHARED_NAME);
        Assert.assertFalse("Non-unique name must be invalid.",
                viewModel.nameValidation().validProperty().get());
    }

    @Test
    public final void testDescriptionValidation() {
        Assert.assertTrue("Must be valid initially.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertTrue("Valid input not recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());

        viewModel.descriptionProperty().set("");
        Assert.assertTrue("Empty string should be recognised as valid.",
                viewModel.descriptionValidation().validProperty().get());
    }
    
    @Test
    public final void testAllValidation() {
        Assert.assertFalse("Must not be valid initially.",
                viewModel.allValidation().validProperty().get());

        viewModel.descriptionProperty().set("Billy Goat");
        Assert.assertFalse("Must not be valid with partial validity.",
                viewModel.allValidation().validProperty().get());
        
        viewModel.nameProperty().set("Name");
        Assert.assertTrue("Must be valid when all contributors are valid.",
                viewModel.allValidation().validProperty().get());
    }
}
