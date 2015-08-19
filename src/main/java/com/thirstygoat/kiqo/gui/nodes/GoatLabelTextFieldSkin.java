package com.thirstygoat.kiqo.gui.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelTextFieldSkin extends GoatLabelSkin {

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelTextFieldSkin(Control control) {
        super(control);
    }

    @Override
    protected void setSizing() {

        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setMinWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);

        TextField textField = ((TextField) editField);
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            Text text = new Text(newValue);
            text.setFont(textField.getFont()); // Set the same font, so the size is the same
            double width = text.getLayoutBounds().getWidth() // This big is the Text in the TextField
                    + textField.getPadding().getLeft() + textField.getPadding().getRight() // Add the padding of the TextField
                    + 2d; // Add some spacing
            textField.setPrefWidth(Math.max(width, 150)); // Set the width
            textField.positionCaret(((TextField) editField).getCaretPosition());
        });
    }

    @Override
    protected Control createEditField() {
        return new TextField();
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_COMPUTED_SIZE);
        editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
        editView.setMinHeight(Control.USE_COMPUTED_SIZE);
        editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
    }

}