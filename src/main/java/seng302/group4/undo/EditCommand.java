package seng302.group4.undo;

import seng302.group4.Project;

/**
 * Sets the shortName of a Project to newval.
 *
 * @author amy
 *
 */
class EditCommand implements Command<Void> {
    private String oldval;
    private final String newval;
    private final Project p;

    EditCommand(final Project project, final String string) {
        this.p = project;
        this.newval = string;
    }

    @Override
    public Void execute() {
        this.oldval = this.p.getShortName();
        this.p.setShortName(this.newval);
        return null;
    }

    @Override
    public void undo() {
        this.p.setShortName(this.oldval);
    }

}
