package com.thirstygoat.kiqo.command;

public class UndoCommand extends Command<Void> {
    private final Command<?> command;

    protected UndoCommand(Command<?> command) {
        this.command = command;
    }

    @Override
    public Void execute() {
        command.undo();
        return null;
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
