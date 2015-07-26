package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.*;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.commands.Action;
import de.saxsys.mvvmfx.utils.commands.Command;
import de.saxsys.mvvmfx.utils.commands.DelegateCommand;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import javax.swing.border.Border;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

        queryProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.trim().length() == 0) {
                results.clear();
            }
        });

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
        try {
            Pattern.compile(query.get());
        } catch (PatternSyntaxException ignored) {
            return;
        }

        // Empty search not allowed
        if (query.get().trim().length() == 0) {
            return;
        }

        Search search = new Search(query.get());
        results.clear();
        results.addAll(search.execute());
    }

    private String getClassString(SearchResult searchResult) {
        return searchResult.getItem().getClass().getSimpleName();
    }

    public Button generateSearchResultRow(SearchResult searchResult) {
        final BorderPane borderPane = new BorderPane();
        final HBox hBox = new HBox();
        hBox.setPrefHeight(24);
        hBox.getChildren().add(borderPane);
        HBox.setHgrow(borderPane, Priority.ALWAYS);

        final Label objectTypeLabel = new Label("(" + getClassString(searchResult) + ")");
        objectTypeLabel.setStyle("-fx-text-fill: #999");
        objectTypeLabel.setAlignment(Pos.CENTER_RIGHT);

        final Label text = new Label(searchResult.getResultText());

        borderPane.setLeft(text);
        borderPane.setRight(objectTypeLabel);

        BorderPane.setAlignment(borderPane.getLeft(), Pos.CENTER_LEFT);
        BorderPane.setAlignment(objectTypeLabel, Pos.CENTER_RIGHT);

        objectTypeLabel.setTextAlignment(TextAlignment.RIGHT);
        objectTypeLabel.setAlignment(Pos.CENTER_RIGHT);

        final Button button = new Button();
        HBox.setHgrow(button, Priority.ALWAYS);
        button.setMaxWidth(Double.MAX_VALUE);

        button.setOnAction(event -> {
            mainController.focusedItemProperty.set(searchResult.getItem());
            stage.close();
        });

        button.getStyleClass().add("searchResultButton");
        button.setGraphic(hBox);
        button.setPadding(new Insets(3, 15, 3, 15));
        button.setDefaultButton(true);
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
