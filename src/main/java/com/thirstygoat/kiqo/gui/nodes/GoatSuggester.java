package com.thirstygoat.kiqo.gui.nodes;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import com.thirstygoat.kiqo.model.Item;

public class GoatSuggester<T extends Item> extends ComboBox<T> {    
    private ObservableList<T> allItems;

    public GoatSuggester() {
        super();        
        allItems = FXCollections.observableArrayList(Item.getWatchStrategy());
        
        style();
        bindShownItems();
    }
    
    private void style() {
//        setMinHeight(30);
//        setStyle("-fx-border-radius: 4 4 0 0; -fx-background-radius: 4 4 0 0;");
        setPromptText("Filter by regex...");
        setEditable(true);
        setVisibleRowCount(8);
    }

    private void bindShownItems() {
        // when text changes, filter list
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            filterList();
            if (!isShowing()) {
                // open the dropdown
                show();
            }
        });
        
        valueProperty().addListener((observable, oldValue, newValue) -> {
            int newIndex = getConverter().toString(newValue).length();
            System.out.println(String.format("new caret position: %d", newIndex));
            TextField editor = getEditor();
            editor.positionCaret(newIndex);
            editor.selectEnd();
        });
        
        // when source list changes, filter list
        allItems.addListener((ListChangeListener<T>) c -> {
            filterList();
        });
    }

    private void filterList() {
        final String regex = Pattern.quote(getEditor().getText().toLowerCase());
        Platform.runLater(() -> {
            getItems().clear();
            getItems().addAll(allItems.stream()
                    .filter(t -> { return t.getShortName().toLowerCase().matches(".*" + regex + ".*"); })
                    .collect(Collectors.toList()));
        });       
    }

    public void setAllItems(ObservableList<T> allItems) {
        this.allItems.clear();
        this.allItems.addAll(allItems);
    }
}
