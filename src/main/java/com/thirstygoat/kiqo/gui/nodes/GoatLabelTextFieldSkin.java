package com.thirstygoat.kiqo.gui.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelTextFieldSkin extends GoatLabelSkin<TextField> {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelTextFieldSkin(GoatLabel<TextField> control) {
        super(control);
    }

    @Override
    protected void setSizing() {

        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setMinWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);

        editField.textProperty().addListener((observable, oldValue, newValue) -> {
            Text text = new Text(newValue);
            text.setFont(editField.getFont()); // Set the same font, so the size is the same
            double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                    + editField.getPadding().getLeft() + editField.getPadding().getRight() // Add the padding of the TextField
                    + 2d; // Add some spacing
            editField.setPrefWidth(width); // Set the width
            editField.positionCaret(editField.getCaretPosition());
        });
    }

    @Override
    protected TextField createEditField() {
        return new TextField() {
            @Override
            public void replaceText(int start, int end, String text) {
                if (restrictToNumericInput) {
                    if (text.matches("[0-9.]") || text.equals("")) {
                        super.replaceText(start, end, text);
                    }
                } else {
                    super.replaceText(start, end, text);
                }

            }

            @Override
            public void replaceSelection(String text) {
                if (restrictToNumericInput) {
                    if (text.matches("[0-9.]") || text.equals("")) {
                        super.replaceSelection(text);
                    }
                } else {
                    super.replaceSelection(text);
                }
            }
        };
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_COMPUTED_SIZE);
        editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
        editView.setMinHeight(Control.USE_COMPUTED_SIZE);
        editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
    }

}