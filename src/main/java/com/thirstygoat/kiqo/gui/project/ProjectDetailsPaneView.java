package com.thirstygoat.kiqo.gui.project;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.detailsPane.AllocationsTableViewController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.util.FxUtils;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bradley on 25/03/2015.
 *
 */
public class ProjectDetailsPaneView implements FxmlView<ProjectDetailsPaneViewModel>, Initializable {
    private MainController mainController;

    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextField descriptionLabel;
    @FXML
    private Button allocateTeamButton;
    @FXML
    private AllocationsTableViewController allocationsTableViewController;

    @InjectViewModel
    private ProjectDetailsPaneViewModel viewModel;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        allocationsTableViewController.init(AllocationsTableViewController.FirstColumnType.TEAM);

        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(),
                viewModel.shortNameValidation());
        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        allocationsTableViewController.setMainController(mainController);
    }
}
