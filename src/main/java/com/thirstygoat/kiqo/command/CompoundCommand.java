package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.util.Utilities;

import java.util.Collection;

/**
 * Wraps several Commands into an atomic unit. Similar to the idea of
 * transactions in database theory.
 *
 * @author bjk60
 */
public class CompoundCommand extends Command {
    private final Collection<Command> commands;
    private String type = "Compound Command";

    /**
     * @param type     short, user-friendly explanation of the functionality
     * @param commands collection of commands to be performed
     */
    public CompoundCommand(String type, final Collection<Command> commands) {
        this.type = type;
        this.commands = commands;
    }

    @Override
    public void execute() {
        commands.forEach(Command::execute);
    }

    @Override
    public String toString() {
        return commands.size() + Utilities.pluralise(commands.size(), " change", " changes");
    }

    @Override
    public void undo() {
        commands.forEach(com.thirstygoat.kiqo.command.Command::undo);
    }

    @Override
    public String getType() {
        return type;
    }

}
