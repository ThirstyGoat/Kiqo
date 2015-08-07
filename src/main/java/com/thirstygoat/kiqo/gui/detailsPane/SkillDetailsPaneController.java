package com.thirstygoat.kiqo.gui.detailsPane;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelView;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelViewModel;
import com.thirstygoat.kiqo.model.Skill;
import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

    private GoatLabelViewModel editableShortName;

    private ViewTuple<GoatLabelView, GoatLabelViewModel> viewTuple;


    @Override
    public void showDetails(final Skill skill) {

//        ViewTuple<GoatLabelView, GoatLabelViewModel> viewTuple = FluentViewLoader.fxmlView(GoatLabelView.class).load();
//        grid.add(viewTuple.getCodeBehind().getGoatLabel(), 1, 2);
//        editableShortName = viewTuple.getViewModel();

        if (skill != null) {
            shortNameLabel.textProperty().bind(skill.shortNameProperty());
            descriptionLabel.textProperty().bind(skill.descriptionProperty());
            editableShortName.displayedTextProperty().bind(skill.shortNameProperty());

            editableShortName.doneProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    skill.setShortName(editableShortName.displayedTextProperty().get());
                    editableShortName.doneProperty.setValue(false);
                }
            });
        } else {

            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            editableShortName.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        viewTuple = FluentViewLoader.fxmlView(GoatLabelView.class).load();
        grid.add(viewTuple.getCodeBehind().getGoatLabel(), 1, 2);
        editableShortName = viewTuple.getViewModel();
    }

    @Override
    public void setMainController(MainController mainController) {
        // don't do it
    }
}
