package com.thirstygoat.kiqo;

import java.io.Serializable;

import javafx.beans.property.StringProperty;

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
