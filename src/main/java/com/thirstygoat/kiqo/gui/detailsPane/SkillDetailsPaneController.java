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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class SkillDetailsPaneController implements Initializable, IDetailsPaneController<Skill> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private GridPane grid;
//    @FXML
//    private GoatLabelView goatLabelView;

    private GoatLabelViewModel editableShortName;


    @Override
    public void showDetails(final Skill skill) {

        System.out.println("show");
        if (skill != null) {
            editableShortName.textProperty().bindBidirectional(skill.shortNameProperty());
            descriptionLabel.textProperty().bind(skill.descriptionProperty());
        } else {
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("init");
        ViewTuple<GoatLabelView, GoatLabelViewModel> viewTuple = FluentViewLoader.fxmlView(GoatLabelView.class).load();
        grid.add(viewTuple.getCodeBehind().getGoatLabel(), 1, 2);
        editableShortName = viewTuple.getViewModel();
    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}
