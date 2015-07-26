package com.thirstygoat.kiqo.model;

/**
 * SearchResult encapsulates a Searchable and allows provides access to
 * Class specific information
 * @author Bradley
 */
public class SearchResult {

    private Searchable searchable;
    private String resultText;

    public SearchResult(Searchable searchable) {
        this.searchable = searchable;
        generateResultText();
    }

    /**
     * Generates the result text to be shown based on the class of Searchable found
     */
    private void generateResultText() {
        if (searchable.getClass() == Allocation.class) {
            Allocation allocation = (Allocation)searchable;
            resultText = "Allocation [" + allocation.getTeam().getShortName() +
                    " on Project: " + allocation.getProject().getShortName() + "]";
        } else {
            resultText = ((Item) searchable).getShortName();
        }
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
