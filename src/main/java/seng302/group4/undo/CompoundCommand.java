package seng302.group4.undo;

import java.util.ArrayList;

/**
 * Overwrites a field value
 *
 * @author bjk60
 *
 */
public class CompoundCommand extends Command<Void> {
    private ArrayList<EditCommand> commands = new ArrayList<>();

    public CompoundCommand(final ArrayList<EditCommand> commands) {
        this.commands = commands;

    }

    @Override
    public Void execute() {
        commands.forEach(seng302.group4.undo.EditCommand::execute);
        return null;
    }

    @Override
    public String toString() {
        return commands.size() + " changes";
    }

    @Override
    public void undo() {
        commands.forEach(seng302.group4.undo.EditCommand::undo);
    }

    public String getType() {
        return "Compound Command";
    }
}
