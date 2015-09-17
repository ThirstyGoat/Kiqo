package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
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
    private final EditButton editButton;
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
        editButton.setVisible(false);
        doneButton = makeDoneButton();
        doneButton.setVisible(false);
        editView = makeEditView();
        editView.setVisible(false);
        displayView = makeDisplayView();
        displayView.setVisible(true);

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

    /**
     * @param parent
     */
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
        editButton.setVisible(false);
        displayView.setVisible(true);
        editView.setVisible(false);
        doneButton.setVisible(false);
        if (cancelButton != null) {
            cancelButton.setVisible(false);
        }
    }

    private EditButton makeEditButton() {
        return new EditButton();
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
            buttonsPane = new StackPane(vBox, editButton);
        } else {
            buttonsPane = new StackPane(doneButton, editButton);
        }
        buttonsPane.setAlignment(Pos.TOP_CENTER);
        HBox parent = new HBox(viewsPane, buttonsPane);
        HBox.setHgrow(viewsPane, Priority.ALWAYS);

        // show/hide pencil
        parent.hoverProperty().addListener(editButton.getFadeAction());
        
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


    class EditButton extends Button {
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
                if (displayView.isVisible()) { // in display mode
                    if (newValue) {
                        fade.setCycleCount(1);
                        fade.playFromStart();
                    } else {
                        fade.setCycleCount(2);
                        fade.playFrom(Duration.millis(400));
                    }
                    setVisible(newValue);
                }
            };
        }
    }
}
