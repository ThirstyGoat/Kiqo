package com.thirstygoat.kiqo.gui.nodes;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;


/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelTextAreaSkin extends GoatLabelSkin<TextArea> {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelTextAreaSkin(GoatLabel<TextArea> control) {
        super(control);
    }

    @Override
    protected void setSizing() {
        displayLabel.maxWidthProperty().bind(mainView.widthProperty());
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);

        ChangeListener listener = (observable, oldValue, newValue) -> {
            // Credit to Daniel van Wichen for working out how to make a textArea resize properly
            Text text = new Text(); // this is necessary to get the height of one row of text
            text.setFont(editField.getFont());
            text.setWrappingWidth(editField.getWidth() - 7.0 - 7.0 - 4.0); // values sourced from Modena.css
            Double rowHeight = text.getLayoutBounds().getHeight(); // the height of one row of text
            text.setText(editField.getText());
            editField.setPrefRowCount((int) Math.round(text.getLayoutBounds().getHeight() / rowHeight));
            stackPane.setPrefHeight(text.getLayoutBounds().getHeight());
        };
        editField.textProperty().addListener(listener);
        editField.widthProperty().addListener(listener);
    }

    @Override
    protected TextArea createEditField() {
        return new TextArea();
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_PREF_SIZE);
        editField.setMaxHeight(Control.USE_PREF_SIZE);
        editView.setMinHeight(Control.USE_PREF_SIZE);
        editView.setMaxHeight(Control.USE_PREF_SIZE);
        editField.setWrapText(true);

        // hiding the scrollbar
        ScrollBar scrollBarv = (ScrollBar)editField.lookup(".scroll-bar:vertical");
        scrollBarv.setMaxWidth(Control.USE_PREF_SIZE);
        scrollBarv.setMinWidth(Control.USE_PREF_SIZE);
        scrollBarv.setPrefWidth(0.5);
        scrollBarv.setDisable(true);
        scrollBarv.setOpacity(0);
    }
}