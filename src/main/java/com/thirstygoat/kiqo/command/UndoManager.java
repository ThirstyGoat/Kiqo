package com.thirstygoat.kiqo.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Manages the undo/redo feature
 *
 * @author amy
 *
 */
public class UndoManager {
    private static final Logger LOGGER = Logger.getLogger(UndoManager.class.getName());
    private final Deque<Command<?>> undoStack = new ArrayDeque<>(), redoStack = new ArrayDeque<>();
    public SimpleBooleanProperty canUndoProperty = new SimpleBooleanProperty(false);
    public SimpleBooleanProperty canRedoProperty = new SimpleBooleanProperty(false);

    public SimpleBooleanProperty shouldUpdateMenuProperty = new SimpleBooleanProperty(false);

    /**
     * Executes the command and adds it to the undo stack.
     *
     * @param <T> return type of the command
     * @param command command to be executed
     * @return return value from command.execute()
     */
    public <T> T doCommand(final Command<T> command) {
        undoStack.push(command);
        redoStack.clear();
        canUndoProperty.set(true);
        canRedoProperty.set(false);
        shouldUpdateMenuProperty.set(true);
        UndoManager.LOGGER.log(Level.INFO, "Doing command %s", command);
        return command.execute();
    }

    /**
     * Redoes the most recently undone command and adds it to the undo stack.
     */
    public void redoCommand() {
        final Command<?> command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        canUndoProperty.set(true);
        canRedoProperty.set(redoStack.size() > 0);
        shouldUpdateMenuProperty.set(true);
        UndoManager.LOGGER.log(Level.INFO, "Redoing command %s", command);
    }

    /**
     * Undoes the most recently executed (or redone) command and adds it to the
     * redo stack.
     */
    public void undoCommand() {
        final Command<?> command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        canRedoProperty.set(true);
        canUndoProperty.set(undoStack.size() > 0);
        shouldUpdateMenuProperty.set(true);
        UndoManager.LOGGER.log(Level.INFO, "Undoing command %s", command);
    }

    /**
     * Empties undo/redo stack. Useful when opening new project and previous commands are now obsolete
     */
    public void empty() {
        undoStack.clear();
        redoStack.clear();
        canUndoProperty.set(false);
        canRedoProperty.set(false);
        shouldUpdateMenuProperty.set(true);
    }

    /**
     * Removes the necessary number of commands from the undo stack and clears the redo stack.
     *
     * @param position number of commands that should remain on undo stack
     */
    public void revert(int position) {
        while (undoStack.size() > position) {
            undoStack.pop();
        }
        redoStack.clear();

        canUndoProperty.set(position > 0);
        canRedoProperty.set(false);
        shouldUpdateMenuProperty.set(true);
    }

    /**
     * @return The size of the undo stack
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    public String getUndoType() {
        return undoStack.peek().getType();
    }

    public String getRedoType() {
        return redoStack.peek().getType();
    }
}
