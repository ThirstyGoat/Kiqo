package seng302.group4.undo;

import java.util.Stack;

import seng302.group4.Project;

public class Main {

    public static void main(final String[] args) {
        final Stack<Command> commandStack = new Stack<Command>();
        final Command<Project> cmd = new CreateProjectCommand("my shortName", "my longName", "/saveLocation");
        commandStack.push(cmd);
        final Project p = cmd.execute();

        System.out.println(p.toString());

        commandStack.pop().undo();

        final EditCommand cmd2 = new EditCommand(p, "NEW");
        commandStack.push(cmd2);
        cmd2.execute();

        System.out.println(p.toString());

        commandStack.pop().undo();

        System.out.println(p.toString());
    };
}
