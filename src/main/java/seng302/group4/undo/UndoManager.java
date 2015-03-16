package seng302.group4.undo;

import java.util.Stack;

import seng302.group4.Project;

public class UndoManager {
    public static void main(final String[] args) {
        final UndoManager undoManager = new UndoManager();
        final Command<Project> cmd = new CreateProjectCommand("my shortName", "my longName", "/saveLocation");
        final Project p = (Project) undoManager.doCommand(cmd);
        System.out.println(p.toString());
        undoManager.undoCommand();
        final Command<Void> cmd2 = new EditCommand(p, "NEW");
        undoManager.doCommand(cmd2);
        System.out.println(p.toString());
    }

    private final Stack<Command> commandStack = new Stack<Command>();

    private Object doCommand(final Command command) {
        this.commandStack.push(command);
        System.out.println(command.toString());
        return command.execute();
    }

    private void undoCommand() {
        final Command command = this.commandStack.pop();
        command.undo();

        System.out.println("Undoing" + command.toString());
    }
}
