package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;


/**
 * Created by james on 26/06/15.
 */
public class RevertTest {
    private UndoManager undoManager;
    private Organisation organisation;

    @Before
    public void setUp() throws Exception {
        organisation = new Organisation();
        undoManager = new UndoManager();
        for (int i = 0; i < 10; i++) {
            Skill skill = new Skill(String.valueOf(i), String.valueOf(i));
            Command<Skill> command1 = new CreateSkillCommand(skill, organisation);
            undoManager.doCommand(command1);
        }
    }

    /**
     * Revert from the top of the stack, branch and save should be 0
     */
    @Test
    public void testBasicRevertFlag() {
        undoManager.revert();
        Assert.assertTrue(undoManager.savePosition == 0);
        Assert.assertTrue(undoManager.branchPosition == 0);
    }

    /**
     * Save as stack size of 10, undo 5 times, revert, savePosition should be 10, branch 10
     */
    @Test
    public void testLessBasicRevertFlag() {
        undoManager.markSavePosition();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.revert();
        Assert.assertTrue(undoManager.savePosition == 10);
        Assert.assertTrue(undoManager.branchPosition == 10);
    }


    /**
     * Have full stack, undo, undo, undo, doCommand (so it has branched), doCommand and branchPosition should be 7
     */
    @Test
    public void testBranchPositionAfterBranching() {
        undoManager.markSavePosition();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        Skill newSkill = new Skill("new skill", "new skill");
        Skill newSkill2 = new Skill("new skill2", "new skill2");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);
        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);
        Assert.assertTrue(undoManager.branchPosition == 7);
    }

    /**
     * Have full stack, undo, undo, undo, doCommand (so it has branched), doCommand and branchPosition should be 7
     * BEST FUNCTION NAME EVER
     */
    @Test
    public void testBranchPositionAfterBranchingAndUndoingPastThatPoint() {
        undoManager.markSavePosition();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        Skill newSkill = new Skill("new skill", "new skill");
        Skill newSkill2 = new Skill("new skill2", "new skill2");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);
        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        Assert.assertTrue(undoManager.branchPosition == 6);
    }

    /**
     * Test revert when past the save position
     */
    @Test
    public void testBasicRevert() {
        undoManager.revert();
        Assert.assertTrue(undoManager.undoStack.size() == 0);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
    }

    /**
     * Test revert when saved, done commands then reverted
     */
    @Test
    public void testBasicRevertPastSavePos() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        Skill newSkill = new Skill("10", "");
        Skill newSkill2 = new Skill("11", "");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);
        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);
        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 10);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 9; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     * Test revert when saved, undone commands then reverted
     */
    @Test
    public void testBasicRevertBeforeSavePos() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 10);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 9; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     * Test revert when saved, undone commands then do commands then revert
     * do do do save undo undo do revert
     */
    @Test
    public void testBranchRevert() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        Skill newSkill = new Skill("10", "");
        Skill newSkill2 = new Skill("11", "");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);

        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();

        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);

        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 10);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 9; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     * Test revert when saved, undone commands then do commands then revert
     * do do do save undo undo do do undo do revert
     */
    @Test
    public void testBranchBranchRevert() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        Skill newSkill = new Skill("10", "");
        Skill newSkill2 = new Skill("11", "");
        Skill newSkill3 = new Skill("12", "");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);
        Command<Skill> newSkillCommand3 = new CreateSkillCommand(newSkill3, organisation);

        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();

        undoManager.doCommand(newSkillCommand);
        //first branch
        undoManager.doCommand(newSkillCommand2);

        undoManager.undoCommand();
        // second branch
        undoManager.doCommand(newSkillCommand3);

        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 10);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 9; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     *
     * do do do save undo undo save do do revert
     */
    @Test
    public void testBranchRevertWithSaves() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        Skill newSkill = new Skill("10", "");
        Skill newSkill2 = new Skill("11", "");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);

        undoManager.undoCommand();
        undoManager.undoCommand();

        undoManager.markSavePosition();

        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);

        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 8);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 7; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     *
     * do do do save undo undo revert do do revert
     */
    @Test
    public void testBranchRevertAndGoPastSavePos() throws NoSuchFieldException, IllegalAccessException {
        undoManager.markSavePosition();
        Skill newSkill = new Skill("10", "");
        Skill newSkill2 = new Skill("11", "");
        Command<Skill> newSkillCommand = new CreateSkillCommand(newSkill, organisation);
        Command<Skill> newSkillCommand2 = new CreateSkillCommand(newSkill2, organisation);

        undoManager.undoCommand();
        undoManager.undoCommand();

        undoManager.revert();

        undoManager.doCommand(newSkillCommand);
        undoManager.doCommand(newSkillCommand2);

        undoManager.revert();

        Assert.assertTrue(undoManager.undoStack.size() == 10);
        Assert.assertTrue(undoManager.redoStack.size() == 0);
        Assert.assertTrue(undoManager.revertStack.size() == 0);

        for (int i = 9; i > 0; i--) {
            Assert.assertTrue(getPrivateSkill((CreateSkillCommand) undoManager.undoStack.pop()).getShortName().equals(String.valueOf(i)));
        }
    }

    /**
     * Helper function to get the private Skill field from a CreateSkillCommand.
     *
     */
    private Skill getPrivateSkill(CreateSkillCommand createSkillCommand) throws NoSuchFieldException, IllegalAccessException {
        Field privateSkill = createSkillCommand.getClass().getDeclaredField("skill");
        privateSkill.setAccessible(true);
        return (Skill) privateSkill.get(createSkillCommand);
    }

}