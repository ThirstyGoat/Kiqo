package com.thirstygoat.kiqo.gui.nodes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Created by samschofield on 7/08/15.
 */
public abstract class GoatLabelSkin<C extends Region> extends SkinBase<Control> {

    protected Label displayLabel;
    protected Label defaultTextLabel;
    protected C editField;
    protected Button editButton;
    protected Button doneButton;
    protected HBox mainView;
    protected StackPane stackPane;
    protected HBox displayView;
    protected HBox editView;
    protected ObjectProperty<EventHandler<ActionEvent>> onCancel = new SimpleObjectProperty<>();
    private StackPane labelStackPane;
    protected boolean restrictToNumericInput = false;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelSkin(@SuppressWarnings("rawtypes") GoatLabel control) {
        super(control);
        mainView = createMainView();
        getChildren().add(mainView);

        hideEditField();

        editField.setMinHeight(5);
        editField.setMaxHeight(5);
        editView.setMinHeight(5);
        editView.setMaxHeight(5);

        setSizing();

        displayView.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                editField.setMinHeight(5);
                editField.setMaxHeight(5);
                editView.setMinHeight(5);
                editView.setMaxHeight(5);
            } else {
                showEditField();
            }
        });
    }

    protected void hideEditField() {
        editField.focusedProperty().addListener((observable, oldValue, newValue) -> {

            if (!newValue && !doneButton.isFocused()) {
                onCancel.get().handle(new ActionEvent());
                showDisplay();
            }
        });
    }

    /**
     * Do any custom resizing etc
     * Set size of everything when you switch to edit mode.
     */
    protected abstract void setSizing();

    /**
     * Creates a new Control to use for editing in edit mode
     */
    protected abstract C createEditField();

    /**
     * Make any changes to the sizing for when the edit field is shown
     */
    protected abstract void showEditField();

    private HBox createMainView() {
        displayView = new HBox();
        displayView.setVisible(true);
        displayView.setSpacing(5);
        displayLabel = new Label();
        displayLabel.setWrapText(true);

        defaultTextLabel = new Label();
        labelStackPane = new StackPane();
        labelStackPane.getChildren().addAll(displayLabel, defaultTextLabel);

        editButton = makeEditButton();
        displayView.getChildren().addAll(labelStackPane, editButton);

        editView = new HBox();
        editView.setVisible(false);
        editField = createEditField();
        doneButton = makeDoneButton();
        editView.getChildren().addAll(editField, doneButton);

        stackPane = new StackPane();
        stackPane.getChildren().addAll(displayView, editView);

        HBox hBox = new HBox();
        hBox.getChildren().add(stackPane);

        return hBox;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDoneButton() {
        return doneButton;
    }

    public final Label getDisplayLabel() {
        return displayLabel;
    }

    public final Label getDefaultTextLabel() {
        return defaultTextLabel;
    }

    public final C getEditField() {
        return editField;
    }

    public final HBox getEditView() {
        return editView;
    }

    public final HBox getDisplayView() {
        return displayView;
    }

    protected void showEdit() {
        displayView.setVisible(false);
        editView.setVisible(true);
    }

    protected void showDisplay() {
        displayView.setVisible(true);
        editView.setVisible(false);
    }

    private Button makeDoneButton() {
        Button button = new Button();
        FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        doneIcon.setStyle("-fx-fill: green");
        button.setGraphic(doneIcon);
        return button;
    }

    private Button makeEditButton() {
        Button button = new Button();
        FontAwesomeIconView pencilIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        pencilIcon.setStyle("-fx-fill: grey");
        button.setGraphic(pencilIcon);
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-padding: 0px;" +
                        "-fx-animated: true;"
        );
        // Make the edit button fade on hover
        final FadeTransition fade = new FadeTransition(Duration.millis(400), button);
        fade.setAutoReverse(true);
        fade.setFromValue(0);
        fade.setToValue(1);
        button.visibleProperty().bind(displayView.hoverProperty());
        displayView.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fade.setCycleCount(1);
                fade.playFromStart();
            } else {
                fade.setCycleCount(2);
                fade.playFrom(Duration.millis(400));
            }
        });
        return button;
    }

    protected void showDebuglines() {
        mainView.setStyle("-fx-border-color: black;");
        displayView.setStyle("-fx-border-color: red;");
        displayLabel.setStyle("-fx-border-color: green;");
        editField.setStyle("-fx-border-color: blue;");
        editView.setStyle("-fx-border-color: pink;");
        stackPane.setStyle("-fx-border-color: purple;");
    }

}