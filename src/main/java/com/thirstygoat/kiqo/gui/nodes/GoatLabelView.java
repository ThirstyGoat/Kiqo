package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.GoatViewModel;
import com.thirstygoat.kiqo.model.Item;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


/**
 * Like GoatLabel, but along MVVM lines.
 * The command is created by the ViewModel rather than the control.
 */
public class GoatLabelView<T extends Item> extends Control {
    public final GoatLabelSkin skin;
    private Label displayLabel;
    private TextField editField;
    private Button editButton;
    private Button doneButton;
    private T item;
    private String fieldName;
    private StringProperty currentVal;
    private Command command;
    private GoatViewModel viewModel;

    public GoatLabelView() {
        super();
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
            command = viewModel.createCommand();
            if (command != null) {
                UndoManager.getUndoManager().doCommand(command);
            }
        });

        editField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();

                if (!viewModel.allValidation().isValid()) {
                    // Don't do anything
                } else {
                    skin.showDisplay();
                    displayLabel.textProperty().unbind();
                    displayLabel.setText(editField.getText());
                    command = viewModel.createCommand();
                    if (command != null) {
                        UndoManager.getUndoManager().doCommand(command);
                    }
                }
            }
        });

    }

    public Button doneButton() {
        return doneButton;
    }

    public StringProperty textProperty() {
        return displayLabel.textProperty();
    }

    public TextField getEditField() {
        return editField;
    }

    public void setViewModel(GoatViewModel viewModel) {
        this.viewModel = viewModel;
    }
    public void setText(String text) {
        displayLabel.textProperty().setValue(text);
    }
}
