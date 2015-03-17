package seng302.group4.exceptions;

import java.beans.IntrospectionException;

/**
 * Thrown when attempting to access a field/property/attribute that does not
 * exist.
 *
 * @author amy
 *
 */
public class FieldNotFoundException extends RuntimeException {
    /**
     * @param fieldName
     *            name of missing field
     * @param e
     *            underlying (checked) exception
     */
    public FieldNotFoundException(final String fieldName, final IntrospectionException e) {
        super("Could not access non-existent field: " + fieldName + ".", e);
    }
}
