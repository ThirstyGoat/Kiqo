package com.thirstygoat.kiqo.model;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.io.Serializable;

/**
 * Represents an object in the model.
 *
 */
public abstract class Item implements Serializable, Searchable {

    public Item() {
        SearchableItems.getInstance().addSearchable(this);
    }

    public static <E extends Item> Callback<E, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty()};
    }

    /**
     * @return non-null unique identifier for this item
     */
    public abstract String getShortName();

    public abstract StringProperty shortNameProperty();

    @Override
    public String[] getSearchableStrings() {
        return new String[] {getShortName()};
    }
}
