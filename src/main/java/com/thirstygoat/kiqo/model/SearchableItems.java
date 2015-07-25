package com.thirstygoat.kiqo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * Created by leroy on 25/07/15.
 */
public class SearchableItems {
    private static SearchableItems instance;
    private ObservableList<Searchable> searchableItems = FXCollections.observableArrayList();

    private SearchableItems() {
    }

    public static SearchableItems getInstance() {
        if (instance == null) {
            instance = new SearchableItems();
        }
        return instance;
    }

    public static void clear() {
        getInstance().searchableItems.clear();
    }

    public void addSearchable(Searchable item) {
        searchableItems.add(item);
    }

    //TODO make readonly
    public ObservableList<Searchable> getSearchables() {
        return searchableItems;
    }

    public void removeSearchable(Searchable searchable) {
        searchableItems.remove(searchable);
    }
}
