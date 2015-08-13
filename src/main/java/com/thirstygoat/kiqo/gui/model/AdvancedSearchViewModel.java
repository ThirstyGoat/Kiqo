package com.thirstygoat.kiqo.gui.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.search.AdvancedSearch;
import com.thirstygoat.kiqo.search.SearchResult;
import com.thirstygoat.kiqo.search.SearchableItems;

import de.saxsys.mvvmfx.ViewModel;

/**
 * Created by bradley on 31/07/15.
 */
public class AdvancedSearchViewModel implements ViewModel {
    private ObservableList<SearchResult> searchResults = FXCollections.observableArrayList();
    private BooleanProperty regexEnabled = new SimpleBooleanProperty();
    private StringProperty searchQuery = new SimpleStringProperty("");
    private ObjectProperty<SearchableItems.SCOPE> searchScope = new SimpleObjectProperty<>();
    private MainController mainController;

    public ObservableList<SearchResult> getSearchResults() {
        return searchResults;
    }

    public void search() {
        String query = searchQueryProperty().get();
        if (searchQueryProperty().get().trim().length() == 0 && !regexEnabledProperty().get())
            return;

        // If we're not doing a RegEx search, then we trim the query of whitespace
        if (!regexEnabledProperty().get()) {
            query = searchQueryProperty().get().trim();
        }

        final AdvancedSearch search = new AdvancedSearch(query, searchScopeProperty().get());
        search.setRegexEnabled(regexEnabledProperty().get());

        searchResults.clear();
        searchResults.addAll(search.execute());
    }

    public BooleanProperty regexEnabledProperty() {
        return regexEnabled;
    }

    public StringProperty searchQueryProperty() {
        return searchQuery;
    }

    public ObjectProperty<SearchableItems.SCOPE> searchScopeProperty() {
        return searchScope;
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void action(SearchResult searchResult) {
        mainController.getDetailsPaneController().showDetailsPane(mainController.focusedItemProperty.get());
        mainController.focusedItemProperty.set(searchResult.getItem());
    }
}