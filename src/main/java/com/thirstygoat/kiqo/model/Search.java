package com.thirstygoat.kiqo.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by leroy on 25/07/15.
 */
public class Search {

    private final String query;

    public Search(String query) {
        this.query = query;
    }

    public ObservableList<Searchable> execute() {
        ObservableList<Searchable> results = FXCollections.observableArrayList();
        for (Searchable searchable : SearchableItems.getInstance().getSearchables()) {
            for (String string : searchable.getSearchableStrings()) {
                if (string.matches(query)) {
                    results.add(searchable);
                }
            }
        }
        return results;
    }
}
