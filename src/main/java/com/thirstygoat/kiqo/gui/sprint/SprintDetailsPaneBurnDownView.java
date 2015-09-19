package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneBurnDownView implements FxmlView<SprintDetailsPaneDetailsViewModel>, Initializable {
    

    @InjectViewModel
    private SprintDetailsPaneDetailsViewModel viewModel;
    


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
