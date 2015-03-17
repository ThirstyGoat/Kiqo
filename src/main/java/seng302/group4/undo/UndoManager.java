package seng302.group4.undo;

import java.util.ArrayDeque;
import java.util.Deque;


public class UndoManager {
    private final Deque<Command> undoStack = new ArrayDeque<>(), redoStack = new ArrayDeque<>();



    public Object doCommand(final Command command) {
        this.undoStack.push(command);
        redoStack.clear();
        System.out.println("Doing " + command.toString());
        return command.execute();
    }

    public void undoCommand() {
        final Command command = this.undoStack.pop();
        command.undo();
        this.redoStack.push(command);

        System.out.println("Undoing " + command.toString());
    }

    public void redoCommand() {
        final Command command = this.redoStack.pop();
        command.redo();
        this.undoStack.push(command);

        System.out.println("Redoing " + command.toString());
    }
}
