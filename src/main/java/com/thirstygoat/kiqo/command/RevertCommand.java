package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Command which (when executed) undoes changes up until the last save position.
 * @author Bradley Kirwan
 */
public class RevertCommand extends Command<Void> {
    private final UndoManager undoManager;
    private final int position;
    private List<Command<?>> commands = new ArrayList<>();

    /**
     * @param undoManager UndoManager
     */
    public RevertCommand(final UndoManager undoManager, final int position) {
        this.undoManager = undoManager;
        this.position = position;
    }

    @Override
    public Void execute() {
        while (undoManager.undoStack.size() > position) {
            Command<?> command = undoManager.undoStack.pop();
            command.undo();
            commands.add(command);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Revert";
    }

    @Override
    public void undo() {
        ListIterator<Command<?>> li = commands.listIterator(commands.size());
        while (li.hasPrevious()) {
            Command<?> command = li.previous();
            undoManager.undoStack.push(command);
            command.execute();
        }
    }

    @Override
    public void redo() {
        commands.clear();
        super.redo();
    }

    @Override
    public String getType() {
        return "Revert";
    }
    
}