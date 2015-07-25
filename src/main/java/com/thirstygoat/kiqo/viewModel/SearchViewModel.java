package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Search;
import com.thirstygoat.kiqo.model.SearchResult;
import com.thirstygoat.kiqo.model.Searchable;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * Created by leroy on 24/07/15.
 */
public class SearchViewModel implements ViewModel {
    private Command searchCommand;
    private BooleanProperty precondition;
    private StringProperty query = new SimpleStringProperty("");
    private ObservableList<SearchResult> results = FXCollections.observableArrayList();
    private MainController mainController;
    private Stage stage;


    public SearchViewModel() {
        // Note: Search command has a .getProgress() property. Maybe this could be bound to a progress bar?
        searchCommand = new DelegateCommand(() -> new Action() {
            @Override
            protected void action() throws Exception {
                search();
            }
        }, precondition, false); // true means a new thread will be created for the action.

    }

    public String getQuery() {
        return query.get();
    }

    public StringProperty queryProperty() {
        return query;
    }

    public ObservableList<SearchResult> getResults() {
        return results;
    }

    private void search() {
        Search search = new Search(query.get());
        results.clear();
        results.addAll(search.execute());
    }

    public Button generateSearchResultRow(SearchResult searchResult) {
        final HBox hBox = new HBox();

        hBox.getChildren().add(new Label(searchResult.getResultText()));

        final Button button = new Button();

        button.setOnAction(event -> {
            mainController.focusedItemProperty.set(searchResult.getItem());
            stage.close();
        });

        button.getStyleClass().add("searchResultButton");
        button.setGraphic(hBox);
        return button;
    }

    public Command getSearchCommand() {
        return searchCommand;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
