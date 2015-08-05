package com.thirstygoat.kiqo.command;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Command which (when executed) undoes changes up until the last save position.
 * @author Bradley Kirwan
 */
public class RevertCommand extends Command {
    private final List<Command> commands = new ArrayList<>();

    /**
     *
     * @param undoManager UndoManager
     * @param position save position in undo stack
     */
    public RevertCommand(final UndoManager undoManager, final int position) {
        int diff = position - undoManager.undoStack.size();
        // only one of these while loops will be executed
        // depending on whether revert takes us forwards or backwards
        while (diff < 0) {
            final Command command = undoManager.undoStack.pop();
            commands.add(command);
            diff++;
        }
        while (diff > 0) {
            final Command command = undoManager.redoStack.pop();
            commands.add(new UndoCommand(command));
            diff--;
        }
    }

    @Override
    public void execute() {
        for (final Command command : commands) {
            command.undo();
        }
    }

    @Override
    public void undo() {
        final ListIterator<Command> li = commands.listIterator(commands.size());
        while (li.hasPrevious()) {
            final Command command = li.previous();
            command.execute(); // or redo?
        }
    }

    @Override
    public String getType() {
        return "Revert";
    }
}