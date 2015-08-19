package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class StoryRowView implements FxmlView<StoryRowViewModel>, Initializable {

    @InjectViewModel
    private StoryRowViewModel viewModel;

    @FXML
    private Label storyNameLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storyNameLabel.textProperty().bind(viewModel.storyNameProperty());
    }
}
