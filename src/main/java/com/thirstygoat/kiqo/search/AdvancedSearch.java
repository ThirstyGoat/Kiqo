package com.thirstygoat.kiqo.search;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.regex.Pattern;

/**
 * Created by leroy on 25/07/15.
 */
public class AdvancedSearch extends Search {

    private boolean regexEnabled = false;
    private SearchableItems.SCOPE scope;

    public AdvancedSearch(String query, SearchableItems.SCOPE scope) {
        super(query);
        this.scope = scope;
    }

    public void setRegexEnabled(boolean regexEnabled) {
        this.regexEnabled = regexEnabled;
    }

    /**
     * Executes the search
     *
     * @return ObservableList<SearchResult> containing the results of the search
     */
    public ObservableList<SearchResult> execute() {
        ObservableList<SearchResult> results = FXCollections.observableArrayList();

        String escapedQuery = Pattern.quote(getQueryLowerCase().trim());

        // Loop through all the Searchable objects in the "database"
        for (Searchable searchable : SearchableItems.getInstance().getSearchables(scope)) {
            // Create the containing SearchResult which will hold all the matches
            SearchResult searchResult = new SearchResult(searchable, getQuery());

            // Check for matches against every string the object allows to be searchable
            for (SearchableField searchableField : searchable.getSearchableStrings()) {

                // If RegEx, perform comparison using String.matches, otherwise use Dice Coefficient algorithm
                if (regexEnabled) {
                    if (searchableField.getFieldValue().matches(getQuery())) {
                        searchResult.addMatch(new Match(searchResult, searchableField, 1.0)); // 1.0 similarity used since [when using RegEx]
                        // results must exactly match the RegEx, therefore matches always have 1.0 similarity
                    }
                } else {
                    if (searchableField.getFieldValue().toLowerCase().matches(".*" + escapedQuery + ".*")) {
                        searchResult.addMatch(new Match(searchResult, searchableField, 0.0));
                    }
                }
            }

            // Check that we actually had a match, if so, add the search result to the list of results
            if (!searchResult.getMatchesUnmodifiable().isEmpty()) {
                results.add(searchResult);
            }
        }
        return results;
    }
}
