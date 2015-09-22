package com.thirstygoat.kiqo.gui.nodes.bicontrol;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private BooleanProperty isInEditMode;

    protected BiControlSkin(BiControl<D, E, T> control, Runnable onCommit, Runnable onCancel, boolean addCancelButton) {
        super(control);        
        this.onCommit = onCommit;
        this.onCancel = onCancel;
        
        editButton = makeEditButton();
        doneButton = makeDoneButton();
        editView = makeEditView();
        displayView = makeDisplayView();

        if (addCancelButton) {
            cancelButton = makeCancelButton();
            cancelButton.setVisible(false);
        } else {
            cancelButton = null;
        }

        attachListeners();

        // initIsInEditMode() must be called *after* attachListeners()
    	isInEditMode = new SimpleBooleanProperty(true);
        initIsInEditMode();
        
        Parent parent = makeParent();
        getChildren().add(parent);
    }

    private void initIsInEditMode() {    	
    	isInEditMode.addListener((observable, oldValue, newValue) -> {
    		if (newValue) {
	            displayView.setVisible(false);
	            editView.setVisible(true);
	            doneButton.setVisible(true);
	            if (cancelButton != null) {
	                cancelButton.setVisible(true);
	            }
    		} else {
    	        displayView.setVisible(true);
    	        editView.setVisible(false);
    	        doneButton.setVisible(false);
    	        if (cancelButton != null) {
    	            cancelButton.setVisible(false);
    	        }
    		}
    	});

        isInEditMode.set(false); // fire change listener to enter display mode to begin
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
        editButton.visibleProperty().bind(isInEditMode.not().and(parent.hoverProperty())); //TODO
        
        return parent;
    }

    protected void onEditAction(ActionEvent event) {
    	enterEditMode();
    }

    protected void onCancelAction(ActionEvent event) {
        isInEditMode.set(false);
        onCancel.run();
    }

    protected void onDoneAction(ActionEvent event) {
        isInEditMode.set(false);
        onCommit.run();
    }
    
    protected void enterEditMode() {
        isInEditMode.set(true);
    }
}