package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;
import javafx.beans.property.StringProperty;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public abstract class GoatLabel<T extends Item, C extends Control, S extends GoatLabelSkin> extends Control {
    protected S skin;
    protected Label displayLabel;
    protected Button editButton;
    protected Button doneButton;
    protected C editField;

    public GoatLabel() {
        super();
        setSkin();
        setButtonBindings();
    }

    protected abstract void setButtonBindings();

    protected abstract void setSkin();

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty displayTextProperty() {
        return displayLabel.textProperty();
    }

    public C getEditField() {
        return editField;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return skin;
    }
}
