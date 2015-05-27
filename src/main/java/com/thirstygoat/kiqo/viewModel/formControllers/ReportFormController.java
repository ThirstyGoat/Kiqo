package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.nodes.GoatListSelectionView;
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

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Carina and James on 27/05/15.
 */
public class ReportFormController implements Initializable {

    private final ObservableList<Item> targetList = FXCollections.observableArrayList();
    private Stage stage;
    private Organisation organisation;
    private boolean valid = false;

    // Begin FXML Injections
    @FXML
    private ComboBox levelComboBox;
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

//        Platform.runLater(shortNameTextField::requestFocus);
    }




    private void setButtonHandlers() {
        okButton.setOnAction(event -> stage.close());

        cancelButton.setOnAction(event -> stage.close());
    }

    private void setListeners() {
        levelComboBox.valueProperty().addListener(((observable, oldValue, newValue) -> {
            elementListSelectionView.getTargetListView().getItems().clear();
            setListSelectionViewData((String)newValue);
        }));
    }

    private void setListSelectionViewData(String newValue) {
        final ObservableList<Item> sourceList = FXCollections.observableArrayList();
        if (newValue == "Projects") {
            sourceList.addAll(organisation.getProjects());
        } else if (newValue == "People") {
            sourceList.addAll(organisation.getPeople());
        } else if (newValue == "Backlogs") {
            for (Project project : organisation.getProjects()) {
                sourceList.addAll(project.getBacklogs());
            }
        } else if (newValue == "Teams") {
            sourceList.addAll(organisation.getTeams());
        }
        elementListSelectionView.setSourceHeader(new Label("Available " + newValue + ":"));
        elementListSelectionView.setTargetHeader(new Label(newValue + " in the report:"));
        elementListSelectionView.getSourceListView().setItems(sourceList);
        elementListSelectionView.getTargetListView().setItems(targetList);
    }

    private void populateComboBox() {
        levelComboBox.getItems().addAll("Projects", "Teams", "People", "Backlogs");
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



//    private void setTargetPeopleCellFactory(ListView<Person> listView) {
//        listView.setCellFactory(view -> new ListCell<Person>() {
//            @Override
//            public void updateItem(Person person, boolean empty) {
//                super.updateItem(person, empty);
//                if (person != null) {
//
//                  }
//                }
//
//
//        });
//    }

//    private void populatePeopleListView() {
//        // all people observableList = project.getPeople();
//        final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
//        sourcePeople.addAll(organisation.getPeople());
//
//        // Remove all people from sourcePeople that are currently in a team
//        organisation.getPeople().stream().filter(person -> person.getTeam() != null).forEach(sourcePeople::remove);
//
//        organisation.getPeople().addListener((ListChangeListener<Person>) c -> {
//            c.next();
//            // We remove people from the sourcePeople that were removed from the project.
//            // Note that this shouldn't actually be possible since undo/redo should be disabled
//            sourcePeople.removeAll(c.getRemoved());
//            targetPeople.removeAll(c.getRemoved());
//            for (final Person person : c.getAddedSubList()) {
//                if (person.getTeam() == team) {
//                    targetPeople.add(person);
//                } else {
//                    sourcePeople.add(person);
//                }
//            }
//        });
//
//        peopleListSelectionView.getSourceListView().setItems(sourcePeople);
//        peopleListSelectionView.getTargetListView().setItems(targetPeople);
//    }

    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}