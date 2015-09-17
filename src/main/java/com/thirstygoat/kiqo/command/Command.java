package com.thirstygoat.kiqo.command;

/**
 * Represents a command to be performed (eg. a modification to the model). Subclasses can accept configuration data
 * through a constructor, if necessary. If execute() does not create an object, T should be set to &lt;Void&gt;.
 *
 * @author amy
 */
public abstract class Command {

    /**
     * Executes the command
     */
    public abstract void execute();

    /**
     * Re-executes the command. DO NOT OVERRIDE THIS METHOD as it is depended upon to be equivalent to execute (ie. execute must not be a precondition of redo).
     * command. Note the lack of a return value, unlike #execute().
     */
    public final void redo() {
        this.execute();
    }

    /**
     * Undoes the command.
     */
    public abstract void undo();

    /**
     * @return user-friendly description of functionality
     */
    public abstract String getType();

    /**
     * @return developer-friendly description of state
     */
    @Override
    public abstract String toString();
}
