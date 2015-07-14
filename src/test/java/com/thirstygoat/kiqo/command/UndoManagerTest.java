package com.thirstygoat.kiqo.command;

import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the UndoManager class
 *
 * @author amy
 * @see UndoManager
 *
 */
public class UndoManagerTest {
    class MockCommand extends Command<Void> {
        boolean done = false;

        @Override
        public final Void execute() {
            done = true;
            return null;
        }

        public boolean isDone() {
            return done;
        }

        @Override
        public void undo() {
            done = false;
        }

        @Override
        public String getType() {
            return "";
        }
    }

    /**
     * Tests that redoCommand throws the correct exception when there is nothing
     * available to redo.
     */
    @Test(expected = NoSuchElementException.class)
    public void testFailedRedo() {
        final UndoManager undoManager = UndoManager.getUndoManager();
        undoManager.empty();
        undoManager.redoCommand();
    }

    /**
     * Tests that undoCommand throws the correct exception when there is nothing
     * available to undo.
     */
    @Test(expected = NoSuchElementException.class)
    public void testFailedUndo() {
        final UndoManager undoManager = UndoManager.getUndoManager();
        undoManager.empty();
        undoManager.undoCommand();
    }

    /**
     * Tests that redoCommand redoes the command.
     */
    @Test
    public void testRedo() {
        final UndoManager undoManager = UndoManager.getUndoManager();
        final MockCommand cmd = new MockCommand();
        undoManager.doCommand(cmd);
        Assert.assertTrue("Command should be executed", cmd.isDone());
        undoManager.undoCommand();
        Assert.assertFalse("Command should be undone", cmd.isDone());
        undoManager.redoCommand();
        Assert.assertTrue("Command should be redone", cmd.isDone());
    }

    /**
     * Tests that undoCommand undoes the command.
     */
    @Test
    public void testUndo() {
        final UndoManager undoManager = UndoManager.getUndoManager();
        final MockCommand cmd = new MockCommand();
        undoManager.doCommand(cmd);
        Assert.assertTrue("Command should be executed", cmd.isDone());
        undoManager.undoCommand();
        Assert.assertFalse("Command should be undone", cmd.isDone());
    }
}
