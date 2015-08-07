package com.thirstygoat.kiqo.gui.nodes;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.FadeTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.controlsfx.glyphfont.GlyphFont;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelViewController implements Initializable {

    @FXML
    private HBox displayView;
    @FXML
    private HBox editView;
    @FXML
    private Button doneButton;
    @FXML
    private Label textLabel;
    @FXML
    private TextField textInput;
    @FXML
    private Button editButton;
    @FXML
    private FontAwesomeIconView pencilIcon;

    public BooleanProperty done = new SimpleBooleanProperty(false);

    private void init() {
        displayView.setVisible(true);
        editView.setVisible(false);
        textLabel.setVisible(true);
        editButton.visibleProperty().bind(displayView.hoverProperty());

//        textInput.textProperty().bind(textLabel.textProperty());

        final FadeTransition fade = new FadeTransition(Duration.millis(400), editButton);
        fade.setAutoReverse(true);
        fade.setFromValue(0);
        fade.setToValue(1);

        displayView.setAlignment(Pos.CENTER_LEFT);
        displayView.setSpacing(5);
        displayView.setMaxWidth(Control.USE_PREF_SIZE);
        displayView.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fade.setCycleCount(1);
                fade.playFromStart();
            } else {
                fade.setCycleCount(2);
                fade.playFrom(Duration.millis(400));
            }
        });

        //TODO move this into CSS?
        pencilIcon.setStyle("-fx-fill: lightcoral");
        editButton.setStyle("" +
                        "-fx-background-color: transparent;" +
                        "-fx-padding: 3px;" +
                        "-fx-animated: true;"
        );

        editButton.setOnAction(event -> {
            displayView.setVisible(false);
            editView.setVisible(true);
            textInput.setText(textLabel.getText());
        });


        doneButton.setOnAction(event -> {
            displayView.setVisible(true);
            editView.setVisible(false);
            textLabel.setText(textInput.getText());
            done.setValue(true);
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public StringProperty textProperty() {
        return textLabel.textProperty();
    }

    public void setText(String text) {
        init();
        textLabel.setText(text);
    }
}
