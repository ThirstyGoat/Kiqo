package com.thirstygoat.kiqo.viewModel.detailControllers;

import java.net.URL;
import java.util.*;

import com.thirstygoat.kiqo.command.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.viewModel.AcceptanceCriteriaListCell;
import com.thirstygoat.kiqo.viewModel.MainController;
import org.controlsfx.control.PopOver;

public class StoryDetailsPaneController implements Initializable, IDetailsPaneController<Story> {

    private MainController mainController;
    private Story story;
    private Map<State, Image> images;
    
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
    @FXML
    private CheckBox isReadyCheckBox;
    @FXML
    private Hyperlink readyWhy;


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

        acListView.setCellFactory(param -> new AcceptanceCriteriaListCell(param, images));
        
        removeACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).greaterThan(1));
        acListView.setItems(story.getAcceptanceCriteria());

        addACButton.setOnAction(event -> mainController.createAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> mainController.editAC(acListView.getSelectionModel().getSelectedItem()));

        isReadyCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (story.getIsReady() != newValue) {
                Command<?> command = new EditCommand<>(story, "isReady", newValue);
                UndoManager.getUndoManager().doCommand(command);
            }
        });
        isReadyCheckBox.setSelected(story.getIsReady());
        story.isReadyProperty().addListener((observable, oldValue, newValue) -> {
            isReadyCheckBox.setSelected(newValue);
        });

        // Story must have at least one AC
        // Story must have non-null estimate
        // Story must be in a backlog

        BooleanBinding nullBacklogBinding = Bindings.isNull(story.backlogProperty());
        BooleanBinding emptyACBinding = Bindings.size(story.getAcceptanceCriteria()).isEqualTo(0);

        // Bind the disable property
        isReadyCheckBox.disableProperty().bind(nullBacklogBinding.or(emptyACBinding));
        readyWhy.visibleProperty().bind(nullBacklogBinding.or(emptyACBinding));

        setIsReadyCheckBoxInfo();
    }

    private void setIsReadyCheckBoxInfo() {
        StringProperty text = new SimpleStringProperty();
        Label label = new Label();
        label.textProperty().bind(text);
        label.setPadding(new Insets(10, 10, 10, 10));
        PopOver readyWhyPopOver = new PopOver(label);
        readyWhyPopOver.setDetachable(false);

        readyWhy.setOnAction((e) -> {
            text.setValue("To mark this Story as Ready, it must:\n\n" +
                            (story.getBacklog() != null ? "✓" : "✘") + " belong to a Backlog\n" +
                            (true ? "✓" : "✘") + " be estimated\n" + // TODO Add once estimation is merged
                            (!story.getAcceptanceCriteria().isEmpty() ? "✓" : "✘") + " have Acceptance Criteria");
            readyWhyPopOver.show(readyWhy);
        });
        readyWhy.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide();
            }
        });
    }

    private void deleteAC() {
        Command command;
        if (acListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command<?>> commands = new ArrayList<>();
            for (AcceptanceCriteria ac : acListView.getSelectionModel().getSelectedItems()) {
                commands.add(new DeleteAcceptanceCriteriaCommand(ac, story));
            }
            command = new CompoundCommand("Delete Acceptance Criteria", commands);
        } else {
            final AcceptanceCriteria acceptanceCriteria = acListView.getSelectionModel().getSelectedItem();
            command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story);
        }

        // Check if they are deleting all of the ACs, and if the story is marked as ready
        // If so, then the command executed must mark the story as no longer ready
        if (acListView.getSelectionModel().getSelectedItems().size() == acListView.getItems().size() &&
                story.getIsReady()) {
            List<Command<?>> changes = new ArrayList<>();
            changes.add(command);
            changes.add(new EditCommand<>(story, "isReady", false));
            command = new CompoundCommand("Delete AC", changes);
        }

        mainController.doCommand(command);
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        images = new HashMap<>();
        final int IMAGE_SIZE = 20;
        final ClassLoader classLoader = getClass().getClassLoader();
        images.put(State.ACCEPTED, new Image(classLoader.getResourceAsStream("images/acceptedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.REJECTED, new Image(classLoader.getResourceAsStream("images/rejectedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.NEITHER, new Image(classLoader.getResourceAsStream("images/noState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        acListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}