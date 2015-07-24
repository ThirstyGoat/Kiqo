package com.thirstygoat.kiqo.viewModel.detailControllers;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.viewModel.AcceptanceCriteriaListCell;
import com.thirstygoat.kiqo.viewModel.MainController;
import com.thirstygoat.kiqo.viewModel.TaskListCell;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;

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
    private Label storyEstimateSliderLabel;
    @FXML
    private Label storyScaleLabel;
    @FXML
    private ListView<AcceptanceCriteria> acListView;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private Slider storyEstimateSlider;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;
    @FXML
    private Button editACButton;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button removeTaskButton;
    @FXML
    private Button editTaskButton;
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

            // need to unbind in case the selected story has changed and therefore we wont try and bind to a bound property
            storyScaleLabel.textProperty().unbind();
            storyScaleLabel.textProperty().bind(story.scaleProperty().asString());
//            storyScaleLabel.textProperty().bind(new When(story.scaleProperty().isNotNull()).then(story.scaleProperty()).otherwise(Scale.FIBONACCI));
            setScale();

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
            storyEstimateSliderLabel.setText(null);
        }

        acListView.setCellFactory(param -> new AcceptanceCriteriaListCell(param, images));
        taskListView.setCellFactory(param -> new TaskListCell(param));

        removeACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        acListView.setItems(story.getAcceptanceCriteria());

        addACButton.setOnAction(event -> mainController.createAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> mainController.editAC(acListView.getSelectionModel().getSelectedItem()));

        removeTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        taskListView.setItems(story.observableTasks());

        addTaskButton.setOnAction(event -> mainController.createTask());
        removeTaskButton.setOnAction(event -> deleteTask());
        editTaskButton.setOnAction(event -> mainController.editTask(taskListView.getSelectionModel().getSelectedItem()));

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
        BooleanBinding noEstimateBinding = Bindings.equal(story.estimateProperty(), 0);

        // Bind the disable property
        isReadyCheckBox.disableProperty().bind(nullBacklogBinding.or(emptyACBinding).or(noEstimateBinding));
        readyWhy.visibleProperty().bind(isReadyCheckBox.disabledProperty());

        setIsReadyCheckBoxInfo();

        // Disable storyEstimateSlider if there are no acceptance criteria.
        storyEstimateSlider.disableProperty().bind(Bindings.isEmpty(acListView.getItems()));
    }

    private void deleteTask() {

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
                            (story.getEstimate() != 0 ? "✓" : "✘") + " be estimated\n" +
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
        Command<?> command;
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
        initSlider();
        acListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        taskListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initSlider() {
        storyEstimateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // set the sliders label to the estimate string if the value > 0
            if (newValue.intValue() > 0) {
                storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(newValue.intValue())-1]);
            } else {
                storyEstimateSliderLabel.setText("-");
            }
        });

        storyEstimateSlider.setOnMouseReleased(event -> {
            if (story.getEstimate() != storyEstimateSlider.getValue()) {
                List<Command<?>> commands = new ArrayList<>();
                if (((int)storyEstimateSlider.getValue()) == 0 && story.getIsReady()) {
                    commands.add(new EditCommand<>(story, "isReady", false));
                }
                commands.add(new EditCommand<>(story, "estimate", (int) storyEstimateSlider.getValue()));
                CompoundCommand command = new CompoundCommand("Edit Estimation", commands);
                UndoManager.getUndoManager().doCommand(command);
            }
        });
    }

    private void setScale() {
        if (story.getScale() == null) {
            // make slider look tidy
            storyEstimateSlider.setValue(0);
            storyEstimateSlider.setMax(1);
            // bind for if they return to null via revert/undo
            storyEstimateSlider.disableProperty().bind(Bindings.isNull(story.scaleProperty()));
        } else {
            // set initial slider value based off story model
            storyEstimateSlider.setValue(story.getEstimate());
            storyEstimateSlider.setMax(story.getScale().getEstimates().length);
            storyEstimateSlider.disableProperty().unbind();
            storyEstimateSlider.setDisable(false);
            // set initial label for slider, value of 0 is null
            if (storyEstimateSlider.getValue() > 0) {
                storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
            }
        }
        // listener for if the scale changes within the story
        story.scaleProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null) {
                if (storyEstimateSlider.getValue() > story.getScale().getEstimates().length) {
                    // set new value to be new max if slider val > new max
                    storyEstimateSlider.setValue(story.getScale().getEstimates().length);
                }
                if (storyEstimateSlider.getValue() > 0) {
                    // if new slider value is greater than 0 (null value for est) set label
                    storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
                }
                storyEstimateSlider.setMax(story.getScale().getEstimates().length);
            } else {
                storyEstimateSlider.setValue(0);
                storyEstimateSlider.setMax(1);
            }
        });
        // set the slider to match what the model has (for redo/undo stuff)
        story.estimateProperty().addListener((observable, oldValue, newValue) -> {
            storyEstimateSlider.setValue(newValue.intValue());
        });
    }
}