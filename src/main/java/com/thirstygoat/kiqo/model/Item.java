package com.thirstygoat.kiqo.model;

import com.thirstygoat.kiqo.gui.nodes.TreeNodeHeading;
import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableItems;
import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents an object in the model.
 *
 */
public abstract class Item implements Serializable, Searchable {

    public Item() {
        if (this.getClass() != TreeNodeHeading.class) {
            SearchableItems.getInstance().addSearchable(this);
        }
    }

    public static <E extends Item> Callback<E, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty()};
    }

    public String getShortName() {
        return shortNameProperty().get();
    };

    public abstract StringProperty shortNameProperty();

    @Override
    public List<String> getSearchableStrings() {
        List<String> searchString = new ArrayList<>();
        searchString.add(getShortName());
        return searchString;
    }
}
