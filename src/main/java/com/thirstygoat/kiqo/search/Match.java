package com.thirstygoat.kiqo.search;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 31/07/15.
 */
public class Match {
    private SearchableField matchedField;
    private double similarity;
    private SearchResult searchResult;

    public Match(SearchResult searchResult, SearchableField matchedField, double similarity) {
        this.searchResult = searchResult;
        this.matchedField = matchedField;
        this.similarity = similarity;
    }

    public String getMatchedString() {
        return matchedField.getFieldValue();
    }

    public SearchableField getMatchedField() {
        return matchedField;
    }

    public double getSimilarity() {
        return similarity;
    }

    public List<int[]> getMatchPositions() {
        String queryLowerCase = searchResult.getSearchQuery().toLowerCase();
        List<int[]> matchSegments = new ArrayList<>();
        int lastIndex = getMatchedString().toLowerCase().indexOf(queryLowerCase);
        while (lastIndex >= 0) {  // indexOf returns -1 if no match found
            matchSegments.add(new int[] {lastIndex, lastIndex + queryLowerCase.length()});
            lastIndex = getMatchedString().toLowerCase().indexOf(queryLowerCase, lastIndex + 1);
        }
        return matchSegments;
    }
}
