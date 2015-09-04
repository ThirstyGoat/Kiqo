package com.thirstygoat.kiqo.gui.nodes;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import de.saxsys.mvvmfx.utils.validation.visualization.*;


/**
 * Created by samschofield on 6/08/15.
 * @param <C> editField type
 */
public abstract class GoatLabel<C extends Control> extends Control {
    protected GoatLabelSkin<C> skin;
    protected Label displayLabel;
    protected Label defaultTextLabel;
    protected C editField;
    protected Button editButton;
    protected Button doneButton;
    private ValidationVisualizer validationVisualizer;
    private ObjectProperty<ValidationStatus> validationStatus;
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>(event -> {});
    private ObjectProperty<EventHandler<ActionEvent>> onCancel = new SimpleObjectProperty<>(event -> {});
    public StringProperty defaultText = new SimpleStringProperty("");

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

        skin.onCancel.bind(this.onCancel);

        setEnterAction();
    }

    /**
     * Performs an action when the enter key is pressed
     * override if custom enter functionality is needed
     */
    protected void setEnterAction() {
        skin.getEditField().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                doneButton.fire();
                event.consume();
            }
        });
    }

    protected void doneAction() {
        onAction.get().handle(new ActionEvent());
        skin.showDisplay();
    }

    protected void setSkin() {
        skin = initSkin();
        displayLabel = skin.getDisplayLabel();
        defaultTextLabel = skin.getDefaultTextLabel();
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

    public void setOnAction(EventHandler<ActionEvent> action) {
        onAction.set(action);
    }

    public EventHandler<ActionEvent> getOnAction() {
        return onAction.get();
    }

    public void setOnCancel(EventHandler<ActionEvent> action) {
        onCancel.set(action);
    }

    public EventHandler<ActionEvent> getOnCancel() {
        return onCancel.get();
    }

    public String getDefaultText() {
        return defaultText.get();
    }

    public StringProperty defaultTextProperty() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText.set(defaultText);
        this.defaultText.set(defaultText);

        defaultTextLabel.setText(defaultText);
        defaultTextLabel.setStyle("-fx-text-fill: grey");

        displayTextProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                displayLabel.setVisible(false);
                displayLabel.setPrefWidth(0.1);
                defaultTextLabel.setVisible(true);
                defaultTextLabel.setPrefWidth(USE_COMPUTED_SIZE);
            } else {
                displayLabel.setVisible(true);
                displayLabel.setPrefWidth(USE_COMPUTED_SIZE);
                defaultTextLabel.setVisible(false);
                defaultTextLabel.setPrefWidth(0.1);
            }
        });
    }
}