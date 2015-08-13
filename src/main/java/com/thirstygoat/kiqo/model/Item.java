package com.thirstygoat.kiqo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableField;


/**
 * Represents an object in the model.
 *
 */
public abstract class Item implements Serializable, Searchable {

    public static <E extends Item> Callback<E, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty()};
    }

    public String getShortName() {
        return shortNameProperty().get();
    };

    public abstract StringProperty shortNameProperty();

    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> searchString = new ArrayList<>();
        searchString.add(new SearchableField("Short Name", getShortName()));
        return searchString;
    }
}
