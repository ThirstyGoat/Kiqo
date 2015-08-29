package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.DatePicker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
* Created by Carina Blair on 8/08/2015.
*/
public class GoatLabelDatePicker extends GoatLabel<DatePicker> {

    public static final String DISABLED_CELL_STYLE = "-fx-background-color: #ffc0cb";

    @Override
    protected GoatLabelSkin<DatePicker> initSkin() {
        return new GoatLabelDatePickerSkin(this);
    }

    @Override
    public DatePicker getEditField() {
        return editField;
    }

    @Override
    protected void populateEditField() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyy");
        editField.setValue(LocalDate.parse(displayLabel.getText(), format));
    }
}
