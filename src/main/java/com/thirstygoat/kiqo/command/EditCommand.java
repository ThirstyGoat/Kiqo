package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.exceptions.FieldNotFoundException;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Overwrites a field value
 *
 * @author amy
 * @param <ModelObjectType> Type of the model object that declares the field
 * @param <FieldType> Type of the field that will be edited
 *
 */
public class EditCommand<ModelObjectType, FieldType> extends Command<Void> {
    private static final Logger LOGGER = Logger.getLogger(EditCommand.class.getName());
    private final Object newVal;
    private final ModelObjectType subject;
    private Object oldVal;
    private PropertyDescriptor propertyDescriptor;
    private String type = "Edit";

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
     * @see com.thirstygoat.kiqo.command.Command#execute()
     */
    @Override
    public Void execute() {
        try {
            this.oldVal = propertyDescriptor.getReadMethod().invoke(subject);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            EditCommand.LOGGER.log(Level.SEVERE, "Can't read old value", e);
        }
        editField(this.newVal);
        return null;
    }

    @Override
    public void undo() {
        editField(this.oldVal);
    }

    @Override
    public String toString() {
        final String newValue = (this.newVal != null) ? this.newVal.toString() : "null";
        return "<Edit " + this.subject.getClass() + ": set " + this.propertyDescriptor.getName() + " to " + newValue + ">";
    }

    /**
     * @param value value with which to set the field
     */
    private void editField(Object value) {
        try {
            final Method writeMethod = this.propertyDescriptor.getWriteMethod();
            EditCommand.LOGGER.log(Level.INFO, "Editing %s via %s", new Object[] {propertyDescriptor.getName(), writeMethod});
            writeMethod.invoke(this.subject, value);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            EditCommand.LOGGER.log(Level.SEVERE, "Can't edit!", e);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
