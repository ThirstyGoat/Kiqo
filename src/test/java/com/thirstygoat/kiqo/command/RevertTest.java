package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Skill;
import junit.framework.TestCase;


/**
 * Created by james on 26/06/15.
 */
public class RevertTest extends TestCase {
    private UndoManager undoManager;
    private Organisation organisation;


    public void setUp() throws Exception {
        super.setUp();
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
    public void testBasicRevert() {
        undoManager.revert();
        assertEquals(true, undoManager.savePosition == 0);
        assertEquals(true, undoManager.branchPosition == 0);
    }

    /**
     * Save as stack size of 10, undo 5 times, revert, savePosition should be 10, branch 5
     */
    public void testLessBasicRevert() {
        undoManager.markSavePosition();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.undoCommand();
        undoManager.revert();
        assertTrue(undoManager.savePosition == 10);
        assertTrue(undoManager.branchPosition == 5);
    }


    /**
     * Have full stack, undo, undo, undo, doCommand (so it has branched), doCommand and branchPosition should be 7
     */
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
        assertTrue(undoManager.branchPosition == 7);
    }

    /**
     * Have full stack, undo, undo, undo, doCommand (so it has branched), doCommand and branchPosition should be 7
     * BEST FUNCTION NAME EVER
     */
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
        System.out.println(undoManager.branchPosition);
        assertTrue(undoManager.branchPosition == 6);
    }
}