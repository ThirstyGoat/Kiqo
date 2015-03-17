package seng302.group4.undo;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the undo/redo feature
 *
 * @author amy
 *
 */
public class UndoManager {
    private final Deque<Command<? extends Object>> undoStack = new ArrayDeque<>(), redoStack = new ArrayDeque<>();

    /**
     * Executes the command and adds it to the undo stack.
     *
     * @param command
     *            command to be executed
     * @return return value from command.execute()
     */
    public <T> T doCommand(final Command<T> command) {
        this.undoStack.push(command);
        this.redoStack.clear();
        System.out.println("Doing " + command.toString());
        return command.execute();
    }

    /**
     * Redoes the most recently undone command and adds it to the undo stack.
     */
    public void redoCommand() {
        final Command<? extends Object> command = this.redoStack.pop();
        command.redo();
        this.undoStack.push(command);

        System.out.println("Redoing " + command.toString());
    }

    /**
     * Undoes the most recently executed (or redone) command and adds it to the
     * redo stack.
     */
    public void undoCommand() {
        final Command<? extends Object> command = this.undoStack.pop();
        command.undo();
        this.redoStack.push(command);

        System.out.println("Undoing " + command.toString());
    }
}
