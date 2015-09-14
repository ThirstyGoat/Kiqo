package com.thirstygoat.kiqo.search;

import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * SearchResult encapsulates a Searchable and allows provides access to
 * Class specific information
 *
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
     *
     * @return Result Text
     */
    public String getResultText() {
        return resultText;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    /**
     * Returns the Item that this search result relates to. (The Item that can be displayed in the details pane)
     * Eg, an AC relates to the Story item that owns it
     *
     * @return Item to be displayed in the details pane
     */
    public Item getItem() {
        if (searchable.getClass() == AcceptanceCriteria.class) {
            return ((AcceptanceCriteria) searchable).getStory();
        } else if (searchable.getClass() == Task.class) {
            return ((Task) searchable).getStory();
        }

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

    public String getType() {
        return searchable.getClass().getSimpleName();
    }
}
