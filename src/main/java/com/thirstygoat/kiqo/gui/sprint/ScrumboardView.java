package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.nodes.scrumboard.Scrumboard;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumboardView implements FxmlView<ScrumboardViewModel>, Initializable {

    @InjectViewModel
    private ScrumboardViewModel viewModel;

    @FXML
    private Scrumboard scrumboard;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewModel.setScrumboard(scrumboard);
    }

    public ScrumboardViewModel getViewModel() {
        return viewModel;
    }
}
