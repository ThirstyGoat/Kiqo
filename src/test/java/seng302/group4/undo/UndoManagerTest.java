package seng302.group4.undo;

import org.junit.Assert;
import org.junit.Test;

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
    }

    /**
     * Tests that UndoManager executes and undoes commands
     */
    @Test
    public void test() {
        final UndoManager undoManager = new UndoManager();
        final MockCommand cmd = new MockCommand();
        undoManager.doCommand(cmd);
        Assert.assertTrue("Command should be executed", cmd.isDone());
        undoManager.undoCommand();
        Assert.assertFalse("Command should be undone", cmd.isDone());
    }
}
