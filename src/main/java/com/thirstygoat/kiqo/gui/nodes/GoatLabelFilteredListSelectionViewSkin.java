package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.Control;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelFilteredListSelectionViewSkin<T extends Item> extends GoatLabelSkin<GoatFilteredListSelectionView<T>> {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelFilteredListSelectionViewSkin(@SuppressWarnings("rawtypes") GoatLabel control) {
        super(control);
    }

    @Override
    protected void hideEditField() {
        editField._focusedProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!editField._focusedProperty().get() && !newValue && !doneButton.isFocused()) {
                    onCancel.get().handle(new ActionEvent());
                    showDisplay();
                }
            });
        });
    }

    @Override
    protected void setSizing() {
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setMinWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);
    }

    @Override
    protected GoatFilteredListSelectionView<T> createEditField() {
        return new GoatFilteredListSelectionView<T>();
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_PREF_SIZE);
        editField.setMaxHeight(Control.USE_PREF_SIZE);
        editView.setMinHeight(Control.USE_PREF_SIZE);
        editView.setMaxHeight(Control.USE_PREF_SIZE);

        // because this seems to be an appropriate size...
        editField.setPrefHeight(200);
        editView.setPrefHeight(200);

        editView.setPrefWidth(Integer.MAX_VALUE);
        editField.setPrefWidth(Integer.MAX_VALUE);
    }

}