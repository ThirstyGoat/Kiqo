package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.scrumboard.ScrumBoard;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumBoardView implements FxmlView<ScrumBoardViewModel>, Initializable {

    @InjectViewModel
    private ScrumBoardViewModel viewModel;

    @FXML
    private ScrumBoard scrumBoard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Pass the scrumboard through to the view model.
        viewModel.setScrumBoard(scrumBoard);
    }

    public ScrumBoardViewModel getViewModel() {
        return viewModel;
    }
}
