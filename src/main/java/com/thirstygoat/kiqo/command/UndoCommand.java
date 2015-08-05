package com.thirstygoat.kiqo.command;

public class UndoCommand extends Command {
    private final Command command;

    protected UndoCommand(Command command) {
        this.command = command;
    }

    @Override
    public void execute() {
        command.undo();
    }

    @Override
    public void undo() {
        command.redo();
    }

    @Override
    public String getType() {
        return "Undo " + command.getType();
    }
}
