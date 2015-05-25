package com.thirstygoat.kiqo.model;

import java.io.Serializable;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

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

    public static final <E extends Item> Callback<E, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty()};
    }
}
