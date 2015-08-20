package com.thirstygoat.kiqo.gui.nodes;

import java.util.function.Supplier;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import com.thirstygoat.kiqo.command.*;

import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import de.saxsys.mvvmfx.utils.validation.visualization.*;


/**
 * Created by samschofield on 6/08/15.
 * @param <C> editField type
 */
public abstract class GoatLabel<C extends Control> extends Control {
    protected GoatLabelSkin<C> skin;
    protected Label displayLabel;
    protected C editField;
    protected Button editButton;
    protected Button doneButton;
    private ValidationVisualizer validationVisualizer;
    private ObjectProperty<ValidationStatus> validationStatus;
    private Supplier<Command> commandSupplier;


    protected abstract GoatLabelSkin<C> initSkin();

    public abstract C getEditField();

    protected abstract void populateEditField();

    public GoatLabel() {
        super();
        setSkin();
        setButtonBindings();
        setValidation();
    }

    private void setValidation() {
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
            if (event.getCode() == KeyCode.ENTER) {
                doneButton.fireEvent(event);
                event.consume();
            }
        });
    }

    protected void doneAction() {
        Command command = commandSupplier.get();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
        skin.showDisplay();
    }

    protected void setSkin() {
        skin = initSkin();
        displayLabel = skin.getDisplayLabel();
        editField = skin.getEditField();
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
    protected GoatLabelSkin<C> createDefaultSkin() {
        return skin;
    }

    public ObjectProperty<ValidationStatus> validationStatus() {
        return validationStatus;
    }

    public void setCommandSupplier(Supplier<Command> supplier) {
        commandSupplier = supplier;
    }
}