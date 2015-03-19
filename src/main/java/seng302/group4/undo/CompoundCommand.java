package seng302.group4.undo;

import java.util.ArrayList;

/**
 * Overwrites a field value
 *
 * @author bjk60
 *
 */
public class CompoundCommand extends Command<Void> {
    private ArrayList<Command<?>> commands = new ArrayList<>();

    public CompoundCommand(final ArrayList<Command<?>> changes) {
        this.commands = changes;
    }

    @Override
    public Void execute() {
        this.commands.forEach(seng302.group4.undo.Command::execute);
        return null;
    }

    @Override
    public String toString() {
        return this.commands.size() + " changes";
    }

    @Override
    public void undo() {
        this.commands.forEach(seng302.group4.undo.Command::undo);
    }

    @Override
    public String getType() {
        return "Compound Command";
    }
}
