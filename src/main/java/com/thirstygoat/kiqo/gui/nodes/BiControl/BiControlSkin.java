package com.thirstygoat.kiqo.gui.nodes.BiControl;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Created by leroy on 16/09/15.
 */
public abstract class BiControlSkin<C extends Region> {
    private class EditButton extends Button {
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

    private final Button editView;
    private final Button displayView;
    private final Button editButton;

    private void registerHoverListener() {
        displayView.visibleProperty().bind(displayView.hoverProperty());
        displayView.hoverProperty().addListener(editButton.getFadeAction());
    }
    protected ObjectProperty<D> displayControl;
    protected ObjectProperty<E> editControl;
    protected ObjectProperty<? extends EventTarget> doneControl;
    private ObjectProperty<EventHandler<ActionEvent>> onAction = new SimpleObjectProperty<>(event -> {});
    private ObjectProperty<EventHandler<ActionEvent>> onCancel = new SimpleObjectProperty<>(event -> {});

    public ObjectProperty<D> displayControl() {
        return displayControl;
    }

    public ObjectProperty<E> editControl() {
        return editControl;
    }




}
