package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.Control;
import javafx.scene.control.TextField;

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
    protected void setResizeListener() {
        displayView.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                editField.setMinHeight(5);
                editField.setMaxHeight(5);
                editView.setMinHeight(5);
                editView.setMaxHeight(5);
            } else {
                editField.setMinHeight(Control.USE_COMPUTED_SIZE);
                editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
                editView.setMinHeight(Control.USE_COMPUTED_SIZE);
                editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
            }
        });
    }

    @Override
    protected Control createEditField() {
        return new TextField();
    }

}
