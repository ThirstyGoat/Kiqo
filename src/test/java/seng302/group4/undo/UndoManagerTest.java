package seng302.group4.undo;

import org.junit.Assert;
import org.junit.Test;

import java.util.NoSuchElementException;

public class UndoManagerTest {
    class MockCommand extends Command<Void> {
        boolean done = false;

        @Override
        public final Void execute() {
            this.done = true;
            return null;
        }

        public boolean isDone() {
            return this.done;
        }

        @Override
        public void undo() {
            this.done = false;
        }

        @Override
        public void redo() {
            this.done = true;
        }
    }


    /**
     * Tests that UndoManager executes and undoes commands
     */
    @Test
    public void testUndo() {
        final UndoManager undoManager = new UndoManager();
        final MockCommand cmd = new MockCommand();
        undoManager.doCommand(cmd);
        Assert.assertTrue("Command should be executed", cmd.isDone());
        undoManager.undoCommand();
        Assert.assertFalse("Command should be undone", cmd.isDone());
    }

    @Test
    public void testRedo() {
        final UndoManager undoManager = new UndoManager();
        final MockCommand cmd = new MockCommand();
        undoManager.doCommand(cmd);
        Assert.assertTrue("Command should be executed", cmd.isDone());
        undoManager.undoCommand();
        Assert.assertFalse("Command should be undone", cmd.isDone());
        undoManager.redoCommand();
        Assert.assertTrue("Command should be redone", cmd.isDone());
    }

    @Test(expected = NoSuchElementException.class)
    public void testFailedRedo() {
        final UndoManager undoManager = new UndoManager();
        undoManager.redoCommand();
    }

    @Test(expected = NoSuchElementException.class)
    public void testFailedUndo() {
        final UndoManager undoManager = new UndoManager();
        undoManager.undoCommand();
    }
}