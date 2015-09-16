package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import de.jensd.fx.glyphs.fontawesome.*;

/**
 * Created by leroy on 16/09/15.
 * @param <D> type of display view
 * @param <E> type of edit view
 * @param <T> type of data to display
 */
public abstract class BiControlSkin<D extends Control, E extends Control, T> extends SkinBase<BiControl<D, E, T>> {
    private static class EditButton extends Button {
        final FadeTransition fade;
        public EditButton() {
            super();
            FontAwesomeIconView pencilIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);

            pencilIcon.setStyle("-fx-fill: grey");
            setGraphic(pencilIcon);
            setStyle("-fx-background-color: transparent;" +
                            "-fx-padding: 0px;" +
                            "-fx-animated: true;");
            // Make the edit button fade on hover
            fade = new FadeTransition(Duration.millis(400), this);
            fade.setAutoReverse(true);
            fade.setFromValue(0);
            fade.setToValue(1);
        }

        protected ChangeListener<Boolean> getFadeAction() {
            return (observable, oldValue, newValue) -> {
                if (newValue) {
                    fade.setCycleCount(1);
                    fade.playFromStart();
                } else {
                    fade.setCycleCount(2);
                    fade.playFrom(Duration.millis(400));
                }
            };
        }
    }

    private final EditButton editButton;
    private final Button doneButton;
    protected final E editView;
    protected final D displayView;

    protected BiControlSkin(BiControl<D, E, T> control) {
        super(control);
        editButton = makeEditButton();
        doneButton = makeDoneButton();
        editView = makeEditView();
        displayView = makeDisplayView();
        Parent parent = new HBox(new StackPane(editView, displayView), new StackPane(editButton, doneButton));

        showDisplayMode();
        /* interactions */
        // show/hide pencil
        parent.hoverProperty().addListener(editButton.getFadeAction());
        editButton.visibleProperty().bind(parent.hoverProperty());
        
        editButton.setOnAction(event -> {
            showEditMode();
        });
        doneButton.setOnAction(event -> {
            showDisplayMode();
        });
        
        getChildren().add(parent);
    }

    private void showEditMode() {
        displayView.setVisible(false);
        editView.setVisible(true);
        doneButton.setVisible(true);
    }
    
    private void showDisplayMode() {
        displayView.setVisible(true);
        editView.setVisible(false);
        doneButton.setVisible(false);
    }

    protected abstract E makeEditView();
    
    protected abstract D makeDisplayView();

    private static EditButton makeEditButton() {
        return new EditButton();
    }
    
    private static Button makeDoneButton() {
        Button button = new Button();
        FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        doneIcon.setStyle("-fx-fill: green");
        button.setGraphic(doneIcon);
        return button;
    }
}
