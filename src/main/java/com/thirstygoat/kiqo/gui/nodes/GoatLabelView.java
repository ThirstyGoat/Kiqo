package com.thirstygoat.kiqo.gui.nodes;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelView implements FxmlView<GoatLabelViewModel>, Initializable {


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
    private HBox goatLabel;

    @InjectViewModel
    private GoatLabelViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        displayView.setVisible(true);
        editView.setVisible(false);
        textLabel.setVisible(true);

        editButton.visibleProperty().bind(displayView.hoverProperty());

        textLabel.textProperty().bindBidirectional(textInput.textProperty());
        textLabel.setText("some text");
        textLabel.setStyle("-fx-alignment: bottom-left");

        final FadeTransition fade = new FadeTransition(Duration.millis(400), editButton);
        fade.setAutoReverse(true);
        fade.setFromValue(0);
        fade.setToValue(1);

        displayView.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                fade.setCycleCount(1);
                fade.playFromStart();
            } else {
                fade.setCycleCount(2);
                fade.playFrom(Duration.millis(400));
            }
        });

        displayView.setSpacing(5);
        editButton.setStyle("" +
                "-fx-background-color: transparent;" +
                "-fx-padding: 3px;" +
                "-fx-animated: true");
        editButton.setMinSize(10, 10);


        editButton.setOnAction(event -> {
            displayView.setVisible(false);
            editView.setVisible(true);
//            textLabel.setVisible(!textLabel.isVisible());
//            textInput.setVisible(!textInput.isVisible());
        });

        doneButton.setOnAction(event -> {
            displayView.setVisible(true);
            editView.setVisible(false);
        });
    }

    
}
