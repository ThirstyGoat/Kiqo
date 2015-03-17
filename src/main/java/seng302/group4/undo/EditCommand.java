package seng302.group4.undo;

import java.lang.reflect.Field;

/**
 * Sets the shortName of a Project to newVal.
 *
 * @author amy
 *
 */
class EditCommand<T extends Object> implements Command<Void> {
    private Object oldVal;
    private final Object newVal;
    private final T subject;
    private Field field;

    EditCommand(final T subject, final String fieldName, final Object newVal) {
        this.subject = subject;
        try {
            this.field = (this.subject.getClass().getDeclaredField(fieldName));
        } catch (NoSuchFieldException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.newVal = newVal;
    }

    @Override
    public Void execute() {
        try {
            this.oldVal = this.field.get(this.subject);
            this.field.set(this.subject, this.newVal);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void undo() {
        try {
            this.field.set(this.subject, this.oldVal);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
