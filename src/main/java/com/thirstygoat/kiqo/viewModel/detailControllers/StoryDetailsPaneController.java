package com.thirstygoat.kiqo.viewModel.detailControllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import com.thirstygoat.kiqo.command.DeleteAcceptanceCriteriaCommand;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.nodes.GoatDialog;
import com.thirstygoat.kiqo.viewModel.MainController;

public class StoryDetailsPaneController implements Initializable, IDetailsPaneController<Story> {

    private MainController mainController;
    private Story story;

    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private ListView<AcceptanceCriteria> acListView;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;
    @FXML
    private Button editACButton;


    @Override
    public void showDetails(final Story story) {
        this.story = story;
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            // This is some seriously cool binding
            // Binding to a property of a property
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));

        } else {
            longNameLabel.textProperty().unbind();
            shortNameLabel.textProperty().unbind();
            descriptionLabel.textProperty().unbind();
            creatorLabel.textProperty().unbind();
            priorityLabel.textProperty().unbind();

            longNameLabel.setText(null);
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            creatorLabel.setText(null);
            priorityLabel.setText(null);
        }

        acListView.setCellFactory(new Callback<ListView<AcceptanceCriteria>, ListCell<AcceptanceCriteria>>() {
            @Override
            public ListCell<AcceptanceCriteria> call(ListView<AcceptanceCriteria> param) {
                return new ListCell<AcceptanceCriteria>() {
                    @Override
                    protected void updateItem(final AcceptanceCriteria item, final boolean empty) {
                        // calling super here is very important

                        if (!empty) {
                            textProperty().bind(item.criteria);
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        acListView.setItems(story.getAcceptanceCriteria());
        addACButton.setOnAction(event -> mainController.createAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> mainController.editAC(acListView.getSelectionModel().getSelectedItem()));
    }

    private void deleteAC() {
        final AcceptanceCriteria acceptanceCriteria = acListView.getSelectionModel().getSelectedItem();
        final DeleteAcceptanceCriteriaCommand command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story);

        final String[] buttons = {"Delete Acceptance Criteria", "Cancel"};
        final String result = GoatDialog.createBasicButtonDialog(mainController.getPrimaryStage(),
                "Delete Acceptance Criteria", "Are you sure?",
                "Are you sure you want to delete the acceptance criteria on this story" + "?", buttons);

        if (result.equals("Delete Acceptance Criteria")) {
            mainController.doCommand(command);
        }
    }

    @Override
    public void setMainController(MainController mainController) {
        // we don't need the main controller for now
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
