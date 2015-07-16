package com.thirstygoat.kiqo.viewModel.formControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.nodes.GoatListSelectionView;

/**
 * Created by Carina and James on 27/05/15.
 */
public class ReportFormController implements Initializable {
    private final ObservableList<Item> targetList = FXCollections.observableArrayList();
    private Stage stage;
    private Organisation organisation;
    private boolean valid = false;
    private Level level = Level.ORGANISATION;

    // Begin FXML Injections
    @FXML
    private ComboBox<String> levelComboBox;
    @FXML
    private GoatListSelectionView<Item> elementListSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        populateComboBox();
        setListeners();
        setListSelectionViewSettings();

        Platform.runLater(levelComboBox::requestFocus);
    }

    public ObservableList<Item> getTargetList() {
        return targetList;
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
                valid = true;
                stage.close();
                    });
        cancelButton.setOnAction(event -> stage.close());
    }

    private void setListeners() {
        levelComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
            elementListSelectionView.getTargetListView().getItems().clear();
            setListSelectionViewData(newValue);
            if (newValue.equals("Organisation")) {
                elementListSelectionView.setDisable(true);
            } else {
                elementListSelectionView.setDisable(false);
            }
        }));

        okButton.disableProperty().bind(Bindings.isEmpty(targetList)
                .and(Bindings.notEqual(levelComboBox.valueProperty(), "Organisation")));
    }

    private void setListSelectionViewData(String newValue) {
        final ObservableList<Item> sourceList = FXCollections.observableArrayList();
        if (newValue.equals("Organisation")) {
            level = Level.ORGANISATION;
        } else if (newValue.equals("Projects")) {
            level = Level.PROJECTS;
            sourceList.addAll(organisation.getProjects());
        } else if (newValue.equals("Teams")) {
            level = Level.TEAMS;
            sourceList.addAll(organisation.getTeams());
        } else if (newValue.equals("People")) {
            level = Level.PEOPLE;
            sourceList.addAll(organisation.getPeople());
        } else if (newValue.equals("Backlogs")) {
            level = Level.BACKLOGS;
            for (Project project : organisation.getProjects()) {
                sourceList.addAll(project.getBacklogs());
            }
        }
        elementListSelectionView.setSourceHeader(new Label("Available " + newValue + ":"));
        elementListSelectionView.setTargetHeader(new Label(newValue + " in the report:"));
        elementListSelectionView.getSourceListView().setItems(sourceList);
        elementListSelectionView.getTargetListView().setItems(targetList);
    }

    private void populateComboBox() {
        levelComboBox.getItems().addAll("Organisation", "Projects", "Teams", "People", "Backlogs");
        levelComboBox.getSelectionModel().select(0);
    }

    private void setListSelectionViewSettings() {
        elementListSelectionView.setPadding(new Insets(0, 0, 0, 0));

        elementListSelectionView.setCellFactories(view -> {
            final ListCell<Item> cell = new ListCell<Item>() {
                public void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getShortName() : null);
                }
            };
            return cell;
        });
    }

    public boolean isValid() {
        return valid;
    }

    public Level getLevel() {
        return level;
    }

    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public enum Level {
        ORGANISATION,
        PROJECTS,
        TEAMS,
        PEOPLE,
        BACKLOGS
    }
}