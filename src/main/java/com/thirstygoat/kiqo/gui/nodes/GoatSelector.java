package com.thirstygoat.kiqo.gui.nodes;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class GoatSelector<T> implements ISelectionView<T>, Initializable {

    private ObjectProperty<ObservableList<T>> allListProperty;
    private ObjectProperty<ObservableList<T>> sourceItemsProperty;
    private ObjectProperty<ObservableList<T>> targetItemsProperty;
    
    @FXML
    TextField textField;
    @FXML
    ListView<T> listView;
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO: bind allListProperty to UNION of sourceItemsProperty and targetItemsProperty
        
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            allListProperty.get().setAll(allListProperty.get().filtered((T t) -> {
                return makeText(t).toLowerCase().contains(textField.textProperty().get().toLowerCase());
            }));
        });
        
        listView.setPlaceholder(new Label("Nothing in this list"));
        listView.setCellFactory((listView) -> {
            return new ListCell<T>() {
                
                private HBox hbox;
                private Label label;
                private CheckBox checkbox;
                
                {
                    label = new Label();
                    checkbox = new CheckBox();
                    hbox = new HBox(label, checkbox);
                    setGraphic(hbox);
                }

                @Override
                protected void updateItem(final T t, final boolean empty) {
                    super.updateItem(t, empty);
                    label.textProperty().set(empty ? "" : makeText(t));
                }
            };
        });
    }
    
    @Override
    public ObjectProperty<ObservableList<T>> getSourceItemsProperty() {
        return sourceItemsProperty;
    }

    @Override
    public ObjectProperty<ObservableList<T>> getTargetItemsProperty() {
        return targetItemsProperty;
    }
    
    private String makeText(T t) {
        return t.toString();
    }
}
