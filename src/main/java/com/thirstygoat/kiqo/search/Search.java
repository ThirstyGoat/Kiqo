package com.thirstygoat.kiqo.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;


/**
 * Created by leroy on 25/07/15.
 */
public class Search {

    private final String query;

    public Search(String query) {
        this.query = query.toLowerCase();
    }

    public String getQuery() {
        return query;
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
            List<String> searchableStrings = searchable.getSearchableStrings();
            for (String string : searchableStrings) {
                // Perform comparison
                if (string.toLowerCase().matches(".*" + query.trim() + ".*")) {
                    results.add(new SearchResult(searchable, query.trim()));
                }
                break;
                // Cheats way of only caring about the first element in the array (if there is one) since we
                // only care about the first searchable string (short name) for this basic search implementation
            }
        }
        return results;
    }
}
