package com.thirstygoat.kiqo.command;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    /**
     * Executes the command and adds it to the undo stack.
     *
     * @param <T> return type of the command
     * @param command command to be executed
     * @return return value from command.execute()
     */
    public <T> T doCommand(final Command<T> command) {
        UndoManager.LOGGER.log(Level.INFO, "Doing command %s", command);
        T result = command.execute();
        undoStack.push(command);
        redoStack.clear();

        updateUndoRedoTypes();

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
    }

    /**
     * Removes the necessary number of commands from the undo stack and clears the redo stack.
     * @param position number of commands that should remain on undo stack
     */
    public void revert(int position) {
        List<Command<?>> changes = new ArrayList<>();

//        while (undoStack.size() > position) {
//            final Command<?> command = undoStack.pop();
//            changes.add(command);
//        }
//
//        // RevertCommand Execute: undoes all the commands in changes
        // RevertCommand Undo: Executes all the commands in changes
        // RevertCommand Redo: undoes all the commands in changes

        RevertCommand rv = new RevertCommand(this, position);
        doCommand(rv);
    }

    /**
     * @return The size of the undo stack
     */
    public int getUndoStackSize() {
        return undoStack.size();
    }

    public String getUndoType() {
        if (undoStack.isEmpty()) {
            return "";
        }
        return undoStack.peek().getType();
    }

    public String getRedoType() {
        if (redoStack.isEmpty()) {
            return "";
        }
        return redoStack.peek().getType();
    }
}
