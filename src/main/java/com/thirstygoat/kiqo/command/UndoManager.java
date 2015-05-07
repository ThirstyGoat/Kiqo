package com.thirstygoat.kiqo.command;

import java.util.ArrayDeque;
import java.util.Deque;

import javafx.beans.property.SimpleBooleanProperty;

/**
 * Manages the undo/redo feature
 *
 * @author amy
 *
 */
public class UndoManager {
    private final Deque<Command<? extends Object>> undoStack = new ArrayDeque<>(), redoStack = new ArrayDeque<>();
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
        System.out.println("Doing " + command.toString());
        return command.execute();
    }

    /**
     * Redoes the most recently undone command and adds it to the undo stack.
     */
    public void redoCommand() {
        final Command<? extends Object> command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        canUndoProperty.set(true);
        canRedoProperty.set(redoStack.size() > 0);
        shouldUpdateMenuProperty.set(true);
        System.out.println("Redoing " + command.toString());
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
        System.out.println("Undoing " + command.toString());
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

    public String getUndoType() {
        return undoStack.peek().getType();
    }

    public String getRedoType() {
        return redoStack.peek().getType();
    }
}
