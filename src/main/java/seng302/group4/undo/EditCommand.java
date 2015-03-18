package seng302.group4.undo;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import seng302.group4.exceptions.FieldNotFoundException;

/**
 * Overwrites a field value
 *
 * @author amy
 *
 */
public class EditCommand<ModelObjectType, FieldType> extends Command<Void> {
    private Object oldVal;
    private final Object newVal;
    private final ModelObjectType subject;
    private PropertyDescriptor propertyDescriptor;

    /**
     * @param subject
     *            model object where the field belongs
     * @param fieldName
     *            name of the field
     * @param newVal
     *            class of the field
     * @throws FieldNotFoundException
     *             if subject does not have a field named fieldName
     */
    public EditCommand(final ModelObjectType subject, final String fieldName, final FieldType newVal) throws FieldNotFoundException {
        this.subject = subject;
        try {
            this.propertyDescriptor = new PropertyDescriptor(fieldName, subject.getClass());
        } catch (final IntrospectionException e) {
            throw new FieldNotFoundException(fieldName, e);
        }
        this.newVal = newVal;
    }

    /**
     * Sets the value of the subject's field named fieldName with newVal, using
     * the field's setter.
     *
     * @see seng302.group4.undo.Command#execute()
     */
    @Override
    public Void execute() {
        try {
            this.oldVal = this.propertyDescriptor.getReadMethod().invoke(this.subject);
            this.propertyDescriptor.getWriteMethod().invoke(this.subject, this.newVal);
        } catch (final IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
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

    @Override
    public String getType() {
        return "Edit";
    }
}
