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

    public void addSearchable(Searchable item) {
        searchableItems.add(item);
    }

    public ObservableList<Searchable> getSearchables() {
        return searchableItems;
    }
}
