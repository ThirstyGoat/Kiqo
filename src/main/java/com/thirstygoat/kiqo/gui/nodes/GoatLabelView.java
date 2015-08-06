package com.thirstygoat.kiqo.gui.nodes;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * Created by samschofield on 6/08/15.
 */
public class GoatLabelView implements FxmlView<GoatLabelViewModel>, Initializable {

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
        textLabel.setText("some text");
        textLabel.setVisible(true);
        textInput.setVisible(false);
        editButton.setVisible(false);
        editButton.visibleProperty().bind(goatLabel.hoverProperty());

        textLabel.textProperty().bindBidirectional(textInput.textProperty());

        editButton.setOnAction(event -> {
            textLabel.setVisible(!textLabel.isVisible());
            textInput.setVisible(!textInput.isVisible());
        });
    }

    
}
