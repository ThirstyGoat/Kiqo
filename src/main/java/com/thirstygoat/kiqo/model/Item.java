package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;
import com.thirstygoat.kiqo.util.BoundPropertySupport;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents an object in the model.
 */
public abstract class Item implements Serializable, Searchable {

    protected final transient BoundPropertySupport bps = new BoundPropertySupport(this);

    public static <E extends Item> Callback<E, Observable[]> getWatchStrategy() {
        return p -> new Observable[]{p.shortNameProperty()};
    }

    public String getShortName() {
        return shortNameProperty().get();
    }

    public void initBoundPropertySupport() {
    }

    public abstract StringProperty shortNameProperty();

    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString = new ArrayList<>();
        searchString.add(new SearchableField("Short Name", getShortName()));
        return searchString;
    }

    public final void addPropertyChangeListener(PropertyChangeListener listener) {
        this.bps.addChangeListener(listener);
    }

    public final void removePropertyChangeListener(PropertyChangeListener listener) {
        this.bps.removeChangeListener(listener);
    }
}
