package com.thirstygoat.kiqo.command;

public abstract class CreateCommand extends Command {

    public CreateCommand() {
        super();
    }

    @Override
    public abstract String getType();

    @Override
    public abstract String toString();

    @Override
    public abstract void undo();

    @Override
    public abstract void execute();
}
