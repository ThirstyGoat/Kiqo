package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelViewModel;
import com.thirstygoat.kiqo.model.Skill;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class SkillDetailsPaneController implements Initializable, IDetailsPaneController<Skill> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private GoatLabelView goatLabelView;


    @Override
    public void showDetails(final Skill skill) {
        if (skill != null) {
            shortNameLabel.textProperty().bind(skill.shortNameProperty());
            descriptionLabel.textProperty().bind(skill.descriptionProperty());
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}
