package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.model.Item;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public abstract class GoatLabel<T extends Item, C extends Control, S extends GoatLabelSkin> extends Control {
    protected S skin;
    protected Label displayLabel;
    protected C editField;
    protected Button editButton;
    protected Button doneButton;
    private ObjectProperty<EditCommand> commandProperty;
    private ValidationVisualizer validationVisualizer;
    private ObjectProperty<ValidationStatus> validationStatus;


    protected abstract S initSkin();

    public abstract C getEditField();

    protected abstract void populateEditField();

    public GoatLabel() {
        super();
        setSkin();
        setButtonBindings();
        setValidation();
    }

    private void setValidation() {
        commandProperty = new SimpleObjectProperty<>();
        validationVisualizer = new ControlsFxVisualizer();
        validationStatus = new SimpleObjectProperty<>();
        validationStatus.addListener((observable, oldValue, newValue) -> {
            doneButton.disableProperty().bind(Bindings.not(validationStatus.get().validProperty()));
            validationVisualizer.initVisualization(validationStatus.get(), editField, true);
        });
    }

    private void setButtonBindings() {
        editButton.setOnAction(event -> {
            skin.showEdit();
            populateEditField();
        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();
            doneAction();
        });

        setEnterAction();
    }

    /**
     * Performs an action when the enter key is pressed
     * override if custom enter functionality is needed
     */
    protected void setEnterAction() {
        skin.getEditField().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER && validationStatus.get().isValid()) {
                event.consume();
                skin.showDisplay();
                doneAction();
            }
        });
    }

    protected void doneAction() {
        try {
            UndoManager.getUndoManager().doCommand(commandProperty.get());
        } catch (Exception e) {
            // TODO remove this (it was only for use in development of this class)
            System.out.println("You should really set the edit command for this label");
        }
    }

    protected void setSkin() {
        skin = initSkin();
        displayLabel = skin.getDisplayLabel();
        editField = (C) skin.getEditField();
        editButton = skin.getEditButton();
        doneButton = skin.getDoneButton();
    }

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty displayTextProperty() {
        return displayLabel.textProperty();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return skin;
    }

    public ObjectProperty<EditCommand> commandProperty() {
        return commandProperty;
    }

    public ObjectProperty<ValidationStatus> validationStatus() {
        return validationStatus;
    }
}
