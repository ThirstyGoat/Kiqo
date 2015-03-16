package seng302.group4.undo;

import java.util.Stack;

public class UndoManager {
    private final Stack<Command> commandStack = new Stack<Command>();

    public Object doCommand(final Command command) {
        this.commandStack.push(command);
        System.out.println("Doing " + command.toString());
        return command.execute();
    }

    public void undoCommand() {
        final Command command = this.commandStack.pop();
        command.undo();

        System.out.println("Undoing " + command.toString());
    }
}
