package seng302.group4.undo;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Overwrites a field value
 *
 * @author amy
 *
 */
public class EditCommand<T extends Object, U extends Object> extends Command<Void> {
    private Object oldVal;
    private final Object newVal;
    private final T subject;
    private PropertyDescriptor propertyDescriptor;

    public EditCommand(final T subject, final String fieldName, final U newVal) {
        this.subject = subject;
        try {
            this.propertyDescriptor = new PropertyDescriptor(fieldName, subject.getClass());
        } catch (SecurityException | IntrospectionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.newVal = newVal;
    }

    @Override
    public Void execute() {
        try {
            this.oldVal = this.propertyDescriptor.getReadMethod().invoke(this.subject);
            this.propertyDescriptor.getWriteMethod().invoke(this.subject, this.newVal);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return "<Edit " + this.subject.getClass() + ": set " + this.propertyDescriptor.getName() + " to " + this.newVal.toString() + ">";
    }

    @Override
    public void undo() {
        try {
            this.propertyDescriptor.getWriteMethod().invoke(this.subject, this.oldVal);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getType() {
        return "Edit";
    }
}
