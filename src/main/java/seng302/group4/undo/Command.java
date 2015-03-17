package seng302.group4.undo;

/**
 * Represents a command to be performed (eg. a modification to the model).
 * Subclasses can accept configuration data through a constructor, if necessary.
 * If execute() does not create an object, T should be set to \<Void\>.
 *
 * @author amy
 *
 * @param <T>
 *            Return type of execute() method
 */
public abstract class Command<T> {

    /**
     * Executes the command
     *
     * @return object created during execution (if any)
     */
    public abstract T execute();

    /**
     * Redoes the command. The default implementation re-executes the original
     * command.
     */
    public void redo() {
        this.execute();
    }

    /**
     * Undoes the command.
     */
    public abstract void undo();
}
