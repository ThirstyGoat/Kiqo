package seng302.group4;

import javafx.beans.property.StringProperty;

import java.io.Serializable;

/**
 * Represents an object in the model.
 *
 */
public abstract class Item implements Serializable {
    /**
     * @return non-null unique identifier for this item
     */
    public abstract String getShortName();

    public abstract StringProperty shortNameProperty();
}
