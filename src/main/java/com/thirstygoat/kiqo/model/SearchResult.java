package com.thirstygoat.kiqo.model;

/**
 * SearchResult encapsulates a Searchable and allows provides access to
 * Class specific information
 * @author Bradley
 */
public class SearchResult {

    private Class searchableClass;
    private Searchable searchable;
    private String resultText;

    public SearchResult(Searchable searchable) {
        this.searchable = searchable;
        this.searchableClass = searchable.getClass();
        generateResultText();
    }

    /**
     * Generates the result text to be shown based on the class of Searchable found
     */
    private void generateResultText() {
        resultText = ((Item)searchable).getShortName();
    }

    /**
     * Returns the result text to be shown in the list of SearchResults
     * @return Result Text
     */
    public String getResultText() {
        return resultText;
    }

    public Item getItem() {
        return (Item) searchable;
    }
}
