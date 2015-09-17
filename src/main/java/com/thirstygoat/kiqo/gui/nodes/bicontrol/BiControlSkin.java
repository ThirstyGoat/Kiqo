package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import de.jensd.fx.glyphs.fontawesome.*;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Parent;
import javafx.util.Duration;

/**
 * Created by leroy on 16/09/15.
 * @param <D> type of display view
 * @param <E> type of edit view
 * @param <T> type of data to display
 */
public abstract class BiControlSkin<D extends Control, E extends Control, T> extends SkinBase<BiControl<D, E, T>> {
    private final Button editButton;
    private final Button doneButton;
    private final Button cancelButton;
    protected final E editView;
    protected final D displayView;
    private Runnable onCommit;
    private Runnable onCancel;

    protected BiControlSkin(BiControl<D, E, T> control, Runnable onCommit, Runnable onCancel, boolean addCancelButton) {
        super(control);
        this.onCommit = onCommit;
        this.onCancel = onCancel;
        
        editButton = makeEditButton();
        doneButton = makeDoneButton();
        editView = makeEditView();
        displayView = makeDisplayView();

        showDisplayView();

        if (addCancelButton) {
            cancelButton = makeCancelButton();
            cancelButton.setVisible(false);
        } else {
            cancelButton = null;
        }
        
        attachListeners();

        Parent parent = makeParent();
        getChildren().add(parent);
    }

    private void attachListeners() {
        // prevent focusTraversal when hidden
        editButton.focusTraversableProperty().bind(editButton.visibleProperty());
        doneButton.focusTraversableProperty().bind(doneButton.visibleProperty());
        editView.focusTraversableProperty().bind(editView.visibleProperty());
        displayView.focusTraversableProperty().bind(displayView.visibleProperty());

        editButton.setOnAction(this::onEditAction);
        doneButton.setOnAction(this::onDoneAction);
        
        if (cancelButton != null) {
            cancelButton.focusTraversableProperty().bind(cancelButton.visibleProperty());
            cancelButton.setOnAction(this::onCancelAction);
        }
    }

    protected void showEditView() {
        editButton.setVisible(false);
        displayView.setVisible(false);
        editView.setVisible(true);
        doneButton.setVisible(true);
        if (cancelButton != null) {
            cancelButton.setVisible(true);
        }
    }
    
    protected void showDisplayView() {
        editButton.setVisible(true);
        displayView.setVisible(true);
        editView.setVisible(false);
        doneButton.setVisible(false);
        if (cancelButton != null) {
            cancelButton.setVisible(false);
        }
    }

    @SuppressWarnings("static-method")
    private Button makeEditButton() {
        Button button = new Button();
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
        icon.setStyle("-fx-fill: grey");   
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0px;");
        button.setGraphic(icon);
        return button;
    }
    
    @SuppressWarnings("static-method")
    private Button makeDoneButton() {
        Button button = new Button();
        FontAwesomeIconView doneIcon = new FontAwesomeIconView(FontAwesomeIcon.CHECK);
        doneIcon.setStyle("-fx-fill: green");
        button.setGraphic(doneIcon);
        return button;
    }

    @SuppressWarnings("static-method")
    private Button makeCancelButton() {
        Button button = new Button();
        FontAwesomeIconView icon = new FontAwesomeIconView(FontAwesomeIcon.CLOSE);
        icon.setStyle("-fx-fill: red");
        button.setGraphic(icon);
        return button;
    }

    protected abstract E makeEditView();

    protected abstract D makeDisplayView();

    protected Parent makeParent() {
        final StackPane viewsPane = new StackPane(editView, displayView);
        final StackPane buttonsPane;
        if (cancelButton != null) {
            final VBox vBox = new VBox(doneButton, cancelButton);
            vBox.setFillWidth(true);
            cancelButton.setMaxWidth(Double.MAX_VALUE);
            buttonsPane = new StackPane(vBox, editButton);
        } else {
            buttonsPane = new StackPane(doneButton, editButton);
        }
        buttonsPane.setAlignment(Pos.TOP_CENTER);
        HBox parent = new HBox(viewsPane, buttonsPane);
        HBox.setHgrow(viewsPane, Priority.ALWAYS);

        // show/hide pencil
//        parent.hoverProperty().addListener(editButton.getFadeAction()); TODO
        
        
        return parent;
    }

    protected void onEditAction(@SuppressWarnings("unused") ActionEvent event) {
        showEditView();
    }

    protected void onCancelAction(@SuppressWarnings("unused") ActionEvent event) {
        showDisplayView();
        onCancel.run();
    }

    protected void onDoneAction(@SuppressWarnings("unused") ActionEvent event) {
        showDisplayView();
        onCommit.run();
    }


    @Deprecated
    class EditButton extends Button {
        final FadeTransition fade;
        
        public EditButton() {
            super();
            FontAwesomeIconView pencilIcon = new FontAwesomeIconView(FontAwesomeIcon.PENCIL);
    
            pencilIcon.setStyle("-fx-fill: grey");
            setGraphic(pencilIcon);
            setStyle("-fx-background-color: transparent;" 
                    + "-fx-padding: 0px;" 
                    + "-fx-animated: true;");
            // Make the edit button fade on hover
            fade = new FadeTransition(Duration.millis(400), this);
            fade.setAutoReverse(true);
            fade.setFromValue(0);
            fade.setToValue(1);
        }
    
        protected ChangeListener<Boolean> getFadeAction() {
            return (observable, oldValue, newValue) -> {
                if (displayView.isVisible()) { // in display mode
                    if (newValue) {
                        fade.setCycleCount(1);
                        fade.playFromStart();
                    } else {
                        fade.setCycleCount(2);
                        fade.playFrom(Duration.millis(400));
                    }
                }
                setVisible(newValue);
            };
        }
    }
}
