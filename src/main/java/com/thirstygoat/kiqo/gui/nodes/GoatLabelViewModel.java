package com.thirstygoat.kiqo.gui.nodes;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelViewModel implements ViewModel {
    private StringProperty displayedText = new SimpleStringProperty("");
    private StringProperty editedTextProperty = new SimpleStringProperty("");
    private BooleanProperty invalidProperty = new SimpleBooleanProperty(false);
    public BooleanProperty doneProperty = new SimpleBooleanProperty(false);

    public GoatLabelViewModel() {

    }

    public void setValidation() {

    }

    public BooleanProperty validProperty() {
        return invalidProperty;
    }

    public StringProperty displayedTextProperty() {
        return displayedText;
    }

    public StringProperty editedTextProperty() {
        return editedTextProperty;
    }

    public void setText(String text) {
        displayedTextProperty().setValue(text);
    }
}
