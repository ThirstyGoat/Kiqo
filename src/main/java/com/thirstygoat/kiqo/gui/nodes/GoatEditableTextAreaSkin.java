package com.thirstygoat.kiqo.gui.nodes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.time.LocalDate;

/**
 * Created by samschofield on 7/08/15.
 */
public class GoatEditableTextAreaSkin extends SkinBase<Control> {

    private Label displayLabel;
    private TextArea editField;

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
    protected GoatEditableTextAreaSkin(Control control) {
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

        displayLabel.maxWidthProperty().bind(mainView.widthProperty());
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        stackPane.setAlignment(Pos.TOP_LEFT);

        displayView.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                editField.setMinHeight(5);
                editField.setMaxHeight(5);
                editView.setMinHeight(5);
                editView.setMaxHeight(5);
            } else {
                editField.setMinHeight(Control.USE_PREF_SIZE);
                editField.setMaxHeight(Control.USE_PREF_SIZE);
                editField.setPrefRowCount(3);
                editView.setMinHeight(Control.USE_PREF_SIZE);
                editView.setMaxHeight(Control.USE_PREF_SIZE);
            }
        });



        //TODO remove
        displayView.setStyle("-fx-border-color: red");
        stackPane.setStyle("-fx-border-color: black");
        displayLabel.setStyle("-fx-border-color: pink");
        editView.setStyle("-fx-border-color: blue");
        mainView.setStyle("-fx-border-color: forestgreen");
    }

    public void something() {
        displayLabel.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
        });
    }



    private HBox createMainView() {

        displayView = new HBox();
        displayView.setVisible(true);
        displayView.setSpacing(5);
        displayLabel = new Label();
        displayLabel.setWrapText(true);
        editButton = makeEditButton();
        displayView.getChildren().addAll(displayLabel, editButton);

        editView = new HBox();
        editView.setVisible(false);
        editField = new TextArea();
        editField.setWrapText(true);
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

    public final TextArea getEditField() {
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
}
