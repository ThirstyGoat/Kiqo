package com.thirstygoat.kiqo.gui.nodes;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelViewModel implements ViewModel {
    private StringProperty textProperty = new SimpleStringProperty("");

    public StringProperty textProperty() {
        return textProperty;
    }
}
