package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardView implements FxmlView<ScrumBoardViewModel>, Initializable {

    @InjectViewModel
    private ScrumBoardViewModel viewModel;

    @FXML
    private VBox scrumBoardVBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //
        viewModel.setScrumBoardVBox(scrumBoardVBox);
    }

    public ScrumBoardViewModel getViewModel() {
        return viewModel;
    }
}
