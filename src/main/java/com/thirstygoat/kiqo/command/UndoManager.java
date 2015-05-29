package com.thirstygoat.kiqo.command;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Manages the undo/redo feature
 *
 * @author amy
 *
 */
public class UndoManager {
    private static final Logger LOGGER = Logger.getLogger(UndoManager.class.getName());
    public final StringProperty undoTypeProperty = new SimpleStringProperty("");
    public final StringProperty redoTypeProperty = new SimpleStringProperty("");
    protected final Deque<Command<?>> undoStack = new ArrayDeque<>(), redoStack = new ArrayDeque<>();
    private final BooleanProperty changesSavedProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty canRevertProperty = new SimpleBooleanProperty(false);
    protected int savePosition = 0;

    /**
     * Executes the command and adds it to the undo stack.
     *
     * @param <T> return type of the command
     * @param command command to be executed
     * @return return value from command.execute()
     */
    public <T> T doCommand(final Command<T> command) {
        UndoManager.LOGGER.log(Level.INFO, "Doing command %s", command);
        final T result = command.execute();
        undoStack.push(command);
        if (command.getClass() != RevertCommand.class) {
            if (undoStack.size() < savePosition) { // behind saveposition
                canRevertProperty.set(false);
            }
            redoStack.clear();
        }

        updateUndoRedoTypes();
        checkChangesSaved();
        return result;
    }

    /**
     * Redoes the most recently undone command and adds it to the undo stack.
     */
    public void redoCommand() {
        final Command<?> command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        updateUndoRedoTypes();
        UndoManager.LOGGER.log(Level.INFO, "Redoing command %s", command);
        checkChangesSaved();
    }

    /**
     * Undoes the most recently executed (or redone) command and adds it to the
     * redo stack.
     */
    public void undoCommand() {
        final Command<?> command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        updateUndoRedoTypes();
        UndoManager.LOGGER.log(Level.INFO, "Undoing command %s", command);
        checkChangesSaved();
    }

    private void updateUndoRedoTypes() {
        undoTypeProperty.set(getUndoType());
        redoTypeProperty.set(getRedoType());
    }

    /**
     * Empties undo/redo stack. Useful when opening new project and previous commands are now obsolete
     */
    public void empty() {
        undoStack.clear();
        redoStack.clear();
        updateUndoRedoTypes();
        savePosition = 0;
        checkChangesSaved();
    }

    /**
     * Creates and executes a RevertCommand to clear the undoStack up to a certain point
     */
    public void revert() {
        doCommand(new RevertCommand(this, savePosition));
    }

    public BooleanProperty changesSavedProperty() {
        return changesSavedProperty;
    }

    private void checkChangesSaved() {
        // If the top of the undostack is a RevertCommand, then that means that the user has just Reverted to last
        // saved version, therefore changes are saved - feels a little hacky - Bradley
        if (!undoStack.isEmpty() && undoStack.peek().getClass() == RevertCommand.class) {
            changesSavedProperty().set(true);
            return;
        }
        changesSavedProperty().set(undoStack.size() == savePosition);
    }

    public String getUndoType() {
        return undoStack.isEmpty() ? "" : undoStack.peek().getType();
    }

    public String getRedoType() {
        return redoStack.isEmpty() ? "" : redoStack.peek().getType();
    }

    public void markSavePosition() {
        savePosition = undoStack.size();
        changesSavedProperty.set(true);
    }
}
