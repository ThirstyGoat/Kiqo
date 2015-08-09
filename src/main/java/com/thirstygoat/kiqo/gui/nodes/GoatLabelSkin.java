package com.thirstygoat.kiqo.gui.nodes;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatLabelSkin extends SkinBase<Control> {

    private Label displayLabel;
    private TextField editField;

    private Button editButton;
    private Button doneButton;

    private HBox mainView;
    private StackPane stackPane;
    private HBox displayView;
    private HBox editView;

    /**
     * Constructor for all SkinBase instances.
     *
     * @param control The control for which this Skin should attach to.
     */
    protected GoatLabelSkin(Control control) {
        super(control);

        mainView = createMainView();
        getChildren().add(mainView);

        editField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !doneButton.isFocused()) {
                showDisplay();
            }
        });

        editField.setMinHeight(5);
        editField.setMaxHeight(5);
        editView.setMinHeight(5);
        editView.setMaxHeight(5);

        displayView.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                editField.setMinHeight(5);
                editField.setMaxHeight(5);
                editView.setMinHeight(5);
                editView.setMaxHeight(5);
            } else {
                editField.setMinHeight(Control.USE_COMPUTED_SIZE);
                editField.setMaxHeight(Control.USE_COMPUTED_SIZE);
                editView.setMinHeight(Control.USE_COMPUTED_SIZE);
                editView.setMaxHeight(Control.USE_COMPUTED_SIZE);
            }
        });
    }

    private HBox createMainView() {

        displayView = new HBox();
        displayView.setVisible(true);

        displayLabel = new Label();

        editButton = new Button();
        FontAwesomeIconView pencilIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        pencilIcon.setStyle("-fx-fill: grey");
        editButton.setGraphic(pencilIcon);
        editButton.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-padding: 3px;" +
                        "-fx-animated: true;"
        );

        // Make the edit button fade on hover
        final FadeTransition fade = new FadeTransition(Duration.millis(400), editButton);
        fade.setAutoReverse(true);
        fade.setFromValue(0);
        fade.setToValue(1);
        editButton.visibleProperty().bind(displayView.hoverProperty());
        displayView.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fade.setCycleCount(1);
                fade.playFromStart();
            } else {
                fade.setCycleCount(2);
                fade.playFrom(Duration.millis(400));
            }
        });

        displayView.getChildren().add(displayLabel);
        displayView.getChildren().add(editButton);

        editView = new HBox();
        editView.setVisible(false);

        editField = new TextField();
        doneButton = new Button();

        FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        doneIcon.setStyle("-fx-fill: green");

        doneButton.setGraphic(doneIcon);

        editView.getChildren().add(editField);
        editView.getChildren().add(doneButton);

        HBox hBox = new HBox();
        stackPane = new StackPane();

        stackPane.getChildren().add(displayView);
        stackPane.getChildren().add(editView);
        hBox.getChildren().add(stackPane);

        displayView.setAlignment(Pos.CENTER_LEFT);
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.setSpacing(5);

        editView.setMaxWidth(Control.USE_COMPUTED_SIZE);
        editField.setMinWidth(100);
        editField.prefWidthProperty().bind(displayLabel.widthProperty());

        stackPane.setAlignment(Pos.CENTER_LEFT);
        displayLabel.maxWidthProperty().bind(hBox.widthProperty());
        displayLabel.setWrapText(true);

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

    public final TextField getEditField() {
        return editField;
    }

    public final HBox getEditView() {
        return editView;
    }

    public final HBox getDisplayView() {
        return displayView;
    }

    public void showEdit() {
        displayView.setVisible(false);
        editView.setVisible(true);
    }

    public void showDisplay() {
        displayView.setVisible(true);
        editView.setVisible(false);
    }

    public void setValidator() {

    }

    public void showDebuglines() {
        mainView.setStyle("-fx-border-color: black;");
        displayView.setStyle("-fx-border-color: red;");
        displayLabel.setStyle("-fx-border-color: green;");
        editField.setStyle("-fx-border-color: blue;");
        editView.setStyle("-fx-border-color: pink;");
        stackPane.setStyle("-fx-border-color: mediumaquamarine;");
    }
}
