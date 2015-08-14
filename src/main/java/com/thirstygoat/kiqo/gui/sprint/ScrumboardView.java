package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by bradley on 14/08/15.
 */
public class ScrumboardView implements FxmlView<ScrumboardViewModel>, Initializable {

    @InjectViewModel
    private ScrumboardViewModel viewModel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public ScrumboardViewModel getViewModel() {
        return viewModel;
    }
}
