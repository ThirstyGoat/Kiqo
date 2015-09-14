package com.thirstygoat.kiqo.command;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

/**
 * Manages the undo/redo feature
 *
 * @author amy
 */
public class UndoManager {
    private static UndoManager instance;

    public final StringProperty undoTypeProperty = new SimpleStringProperty("");
    public final StringProperty redoTypeProperty = new SimpleStringProperty("");
    protected final Deque<Command> undoStack = new ArrayDeque<>();
    protected final Deque<Command> redoStack = new ArrayDeque<>();
    protected final Deque<Command> revertStack = new ArrayDeque<>();
    protected final ObservableList<Command> saveUndoStack = FXCollections.observableArrayList();
    private final BooleanProperty changesSavedProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty canRevertProperty = new SimpleBooleanProperty(false);
    protected int savePosition = 0;
    protected int branchPosition = 0;


    private UndoManager() {
    }

    public static UndoManager getUndoManager() {
        if (instance == null) {
            instance = new UndoManager();
        }
        return instance;
    }

    /**
     * Executes the command and adds it to the undo stack.
     *
     * @param command command to be executed
     */
    public void doCommand(final Command command) {
        command.execute();
        undoStack.push(command);
        if (undoStack.size() < savePosition) { // behind saveposition
            canRevertProperty.set(false);
        }
        redoStack.clear();

        updateUndoRedoTypes();
        checkChangesSaved();
    }

    /**
     * Redoes the most recently undone command and adds it to the undo stack.
     */
    public void redoCommand() {
        final Command command = redoStack.pop();
        command.redo();
        undoStack.push(command);
        updateUndoRedoTypes();
        checkChangesSaved();
    }

    /**
     * Undoes the most recently executed (or redone) command and adds it to the
     * redo stack.
     */
    public void undoCommand() {
        final Command command = undoStack.pop();
        command.undo();
        redoStack.push(command);
        updateUndoRedoTypes();
        checkChangesSaved();
        if (undoStack.size() < savePosition && undoStack.size() < branchPosition) {
            branchPosition = undoStack.size();
            revertStack.push(command);
        }
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
//        doCommand(new RevertCommand(this));
        int diff = savePosition - undoStack.size();
        // only one of these while loops will be executed
        // depending on whether revert takes us forwards or backwards
        redoStack.clear();
        while (diff < 0) {
            final Command command = undoStack.pop();
            command.undo();
            diff++;
        }
        while (undoStack.size() > branchPosition) {
            undoStack.pop().undo();
        }
        while (revertStack.size() > 0) {
            doCommand(revertStack.pop());
        }
        branchPosition = undoStack.size();
        updateUndoRedoTypes();

        checkChangesSaved();
    }

    public BooleanProperty changesSavedProperty() {
        return changesSavedProperty;
    }

    private void checkChangesSaved() {
        // because there aren't equal methods for
        boolean unsavedChanges = true;
        ArrayList<Command> temp = new ArrayList<>(undoStack);
        if (saveUndoStack.size() == undoStack.size()) {
            for (int i = 0; i < undoStack.size(); i++) {
                if (saveUndoStack.get(i) != temp.get(i)) {
                    unsavedChanges = false;
                    break;
                }
            }
        } else {
            unsavedChanges = false;
        }

        changesSavedProperty.setValue(unsavedChanges);
    }

    public String getUndoType() {
        return undoStack.isEmpty() ? "" : undoStack.peek().getType();
    }

    public String getRedoType() {
        return redoStack.isEmpty() ? "" : redoStack.peek().getType();
    }

    public void markSavePosition() {
        savePosition = undoStack.size();
        branchPosition = undoStack.size();
        revertStack.clear();
        saveUndoStack.setAll(undoStack);
        changesSavedProperty.set(true);
    }
}
