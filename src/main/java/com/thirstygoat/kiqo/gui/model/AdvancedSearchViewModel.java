package com.thirstygoat.kiqo.gui.model;

import com.thirstygoat.kiqo.search.Search;
import com.thirstygoat.kiqo.search.SearchResult;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by bradley on 31/07/15.
 */
public class AdvancedSearchViewModel implements ViewModel {
    private ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();

    public ObservableList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void search(String query) {
        final Search search = new Search(query);
        searchResults.clear();
        searchResults.addAll(search.execute());
    }
}
