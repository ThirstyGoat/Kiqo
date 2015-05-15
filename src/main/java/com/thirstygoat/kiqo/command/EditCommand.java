package com.thirstygoat.kiqo.command;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.thirstygoat.kiqo.exceptions.FieldNotFoundException;

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
        final String newValue = (this.newVal != null) ? this.newVal.toString() : "null";
        return "<Edit " + this.subject.getClass() + ": set " + this.propertyDescriptor.getName() + " to " + newValue + ">";
    }

    @Override
    public void undo() {
        try {
            final Method writeMethod = this.propertyDescriptor.getWriteMethod();
            EditCommand.LOGGER.log(Level.INFO, "Editing via %s", writeMethod);
            writeMethod.invoke(this.subject, this.oldVal);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            EditCommand.LOGGER.log(Level.SEVERE, "Can't edit", e);
        }
    }

    @Override
    public String getType() {
        return "Edit";
    }
}
