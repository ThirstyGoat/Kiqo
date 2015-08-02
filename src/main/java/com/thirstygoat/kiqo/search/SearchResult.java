package com.thirstygoat.kiqo.search;

import com.thirstygoat.kiqo.model.Item;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * SearchResult encapsulates a Searchable and allows provides access to
 * Class specific information
 * @author Bradley
 */
public class SearchResult {

    private Searchable searchable;
    private String resultText;
    private String searchQuery;
    private List<Match> matches = new ArrayList<>();

    public SearchResult(Searchable searchable, String searchQuery) {
        this.searchable = searchable;
        this.searchQuery = searchQuery;
        generateResultText();
    }

    /**
     * Generates the result text to be shown based on the class of Searchable found
     */
    private void generateResultText() {
        resultText = ((Item) searchable).getShortName();
    }

    /**
     * Returns the result text to be shown in the list of SearchResults
     * @return Result Text
     */
    public String getResultText() {
        return resultText;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public Item getItem() {
        return (Item) searchable;
    }

    public void addMatch(Match match) {
        matches.add(match);
    }

    public List<Match> getMatches() {
        return matches;
    }

    public List<Match> getMatchesUnmodifiable() {
        return Collections.unmodifiableList(matches);
    }
}