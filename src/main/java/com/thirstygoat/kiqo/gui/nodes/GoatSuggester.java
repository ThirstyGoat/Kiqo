package com.thirstygoat.kiqo.gui.nodes;

import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.util.StringConverters;

public class GoatSuggester<T extends Item> extends ComboBox<T> {    
    private ListProperty<T> sourceList;
    private FilteredList<T> filteredList;

    public GoatSuggester() {
        super();        
        sourceList = new SimpleListProperty<>();
        filteredList = new FilteredList<>(sourceList);
        setItems(filteredList);
        
        style();
        addBehaviour();
    }
    
    private void style() {
//        setMinHeight(30);
//        setStyle("-fx-border-radius: 4 4 0 0; -fx-background-radius: 4 4 0 0;");
        setEditable(true);
        setVisibleRowCount(8);
        setPrefWidth(Integer.MAX_VALUE); // grow to fill available space
    }

    private void addBehaviour() {
        // when selected item changes, move caret to end of text
        getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            getEditor().end();
        });
        
        // when text changes, replace filter
        getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isShowing() && isFocused()) {
                show(); // open the dropdown
            }
            // wrap in runLater to avoid odd IndexOutOfBoundsException when a suggestion is clicked
            Platform.runLater(() -> {
                filteredList.setPredicate(t -> { return t.getShortName()
                        .toLowerCase().matches(".*" + Pattern.quote(newValue.toLowerCase()) + ".*"); });
            });
        });
    }

    public void setSource(ObservableList<T> source) {
        setConverter(StringConverters.stringConverter(source));
        sourceList.set(source);
    }
}
