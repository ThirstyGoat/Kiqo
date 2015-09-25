package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Carina and James on 27/05/15.
 */
public class ReportFormController implements Initializable {
    private Stage stage;
    private Organisation organisation;
    private boolean valid = false;
    private Level level = Level.ORGANISATION;
    private ListProperty<Item> allItems;
    private ListProperty<Item> selectedItems;

    // Begin FXML Injections
    @FXML
    private ComboBox<String> levelComboBox;
    @FXML
    private GoatFilteredListSelectionView<Item> elementListSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    public void initialize(URL location, ResourceBundle resources) {
        allItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    	selectedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    	elementListSelectionView.bindAllItems(allItems);
        elementListSelectionView.bindSelectedItems(selectedItems);
        elementListSelectionView.setStringPropertyCallback(Item::shortNameProperty);
        
        setButtonHandlers();
        populateComboBox();
        setListeners();
        Platform.runLater(levelComboBox::requestFocus);
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
        	selectedItems.clear();
            setListSelectionViewData(newValue);
        })); 

        okButton.disableProperty().bind(Bindings.isEmpty(selectedItems)
                .and(Bindings.notEqual(levelComboBox.valueProperty(), "Organisation")));
        elementListSelectionView.disableProperty().bind(Bindings.equal(levelComboBox.valueProperty(), "Organisation"));
    }

    private void setListSelectionViewData(String newValue) {
        if (newValue.equals("Organisation")) {
            level = Level.ORGANISATION;
            allItems.clear();
        } else if (newValue.equals("Projects")) {
            level = Level.PROJECTS;
            allItems.setAll(organisation.getProjects());
        } else if (newValue.equals("Teams")) {
            level = Level.TEAMS;
            allItems.setAll(organisation.getTeams());
        } else if (newValue.equals("People")) {
            level = Level.PEOPLE;
            allItems.setAll(organisation.getPeople());
        } else if (newValue.equals("Backlogs")) {
            level = Level.BACKLOGS;
            allItems.setAll(organisation.getProjects().stream().flatMap(project -> project.getBacklogs().stream()).collect(Collectors.toList()));
        }
    }

    private void populateComboBox() {
        levelComboBox.getItems().addAll("Organisation", "Projects", "Teams", "People", "Backlogs");
        levelComboBox.getSelectionModel().select(0);
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

    public List<Item> selectedItems() {
        return selectedItems;
    }
}
