package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.Skill;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabel extends Control {
    public final GoatLabelSkin skin;
    private Label displayLabel;
    private TextField editField;
    private Button editButton;
    private Button doneButton;
    private BooleanProperty validProperty = new SimpleBooleanProperty();
    private Skill skill;

    private EditCommand command;


    public GoatLabel() {
        super();
        validProperty.setValue(true);
        skin = new GoatLabelSkin(this) {
            {
                displayLabel = getDisplayLabel();
                editField = getEditField();
                editButton = getEditButton();
                doneButton = getDoneButton();
            }
        };

        setSkin(skin);

        editButton.setOnAction(event -> {
            skin.showEdit();
            editField.setText(displayLabel.getText());
        });

        doneButton.setOnAction(event -> {
            skin.showDisplay();

            displayLabel.textProperty().unbind();
            displayLabel.setText(editField.getText());

            if (!editField.getText().equals(skill.getShortName())) {
                command = new EditCommand<>(skill, "shortName", editField.getText());
                UndoManager.getUndoManager().doCommand(command);
            }

        });

        editField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                skin.showDisplay();
            }
        });
    }

    public Button doneButton() {
        return doneButton;
    }

    public BooleanProperty validProperty() {
        return validProperty;
    }

    public void setValidator() {

    }

    public StringProperty textProperty() {
        return displayLabel.textProperty();
    }

    public TextField getEditField() {
        return editField;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
