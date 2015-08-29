package com.thirstygoat.kiqo.gui.nodes;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

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
