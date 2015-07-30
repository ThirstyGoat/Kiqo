package com.thirstygoat.kiqo.search;

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

    /**
     * Executes the search
     * @return ObservableList<SearchResult> containing the results of the search
     */
    public ObservableList<SearchResult> execute() {
        ObservableList<SearchResult> results = FXCollections.observableArrayList();

        // Loop through all the Searchable objects in the "database"
        for (Searchable searchable : SearchableItems.getInstance().getSearchables()) {

            // Check for matches against every string the object allows to be searchable
            for (String string : searchable.getSearchableStrings()) {

                // Perform comparison
                if (string.toLowerCase().matches(".*" + query.toLowerCase().trim() + ".*")) {
                    results.add(new SearchResult(searchable));
                }
            }
        }
        return results;
    }
}
