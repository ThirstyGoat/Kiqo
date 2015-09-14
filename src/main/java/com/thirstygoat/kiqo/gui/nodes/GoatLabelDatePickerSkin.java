package com.thirstygoat.kiqo.gui.nodes;

import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;

/**
 * Created by Carina Blair on 8/08/2015.
 */
public class GoatLabelDatePickerSkin extends GoatLabelSkin<DatePicker> {


    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelDatePickerSkin(@SuppressWarnings("rawtypes") GoatLabel control) {
        super(control);
    }

    @Override
    protected void setSizing() {
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setMinWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);
    }

    @Override
    protected DatePicker createEditField() {
        return new DatePicker();
    }

    @Override
    protected void showEditField() {
        editField.setMinHeight(Control.USE_COMPUTED_SIZE);
        editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
        editView.setMinHeight(Control.USE_COMPUTED_SIZE);
        editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
    }
}
