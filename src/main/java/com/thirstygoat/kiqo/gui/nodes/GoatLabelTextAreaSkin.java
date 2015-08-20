package com.thirstygoat.kiqo.gui.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;


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
    }

    @Override
    protected TextArea createEditField() {
        return new TextArea();
    }

    @Override
    protected void showEditField() {
        TextArea textArea = editField;
        textArea.setMinHeight(Control.USE_PREF_SIZE);
        textArea.setMaxHeight(Control.USE_PREF_SIZE);
        textArea.setPrefRowCount(textArea.getText().split("\n").length);
        textArea.textProperty().addListener((observable1, oldValue1, newValue1) -> {
            String s = newValue1;
            char c = '\n';
            textArea.setPrefRowCount(Math.max(s.replaceAll("[^" + c + "]", "").length(), 1));
        });
        editView.setMinHeight(Control.USE_PREF_SIZE);
        editView.setMaxHeight(Control.USE_PREF_SIZE);

        textArea.setWrapText(true);
        ScrollBar scrollBarv = (ScrollBar)editField.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);
        scrollBarv.setOpacity(0);
    }
}