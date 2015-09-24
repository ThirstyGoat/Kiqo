package com.thirstygoat.kiqo.gui.story;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.delete.DeleteAcceptanceCriteriaCommand;
import com.thirstygoat.kiqo.command.delete.DeleteTaskCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.AcceptanceCriteriaListCell;
import com.thirstygoat.kiqo.gui.customCells.TaskListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.nodes.bicontrol.FilteredListBiControl;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class StoryDetailsPaneView implements FxmlView<StoryDetailsPaneViewModel>, Initializable {

    public static AcceptanceCriteria draggingAC;
    public static Task draggingTask;
    private MainController mainController;
    private ObjectProperty<Story> story = new SimpleObjectProperty<>();
    private Map<State, Image> images;
    private ChangeListener<Boolean> isReadyListener;
    private ChangeListener<Boolean> modelIsReadyListener;
    private FloatProperty tasksHoursProperty = new SimpleFloatProperty(0.0f);
    @FXML
    private GoatLabelTextField shortNameLabel;
    @FXML
    private GoatLabelTextField longNameLabel;
    @FXML
    private GoatLabelTextField descriptionLabel;
    @FXML
    private GoatLabelTextField creatorLabel;
    @FXML
    private FilteredListBiControl<ListView<Story>, Story> dependenciesLabel;
    @FXML
    private GoatLabelTextField priorityLabel;
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
    @FXML
    private Label totalHoursLabel;
    @FXML
    private Hyperlink estimateWhy;
    @FXML
    private Label totalLoggedHours;
    @FXML
    private AnchorPane mainAnchorPane;
    @FXML
    private VBox detailsVbox;
    @FXML
    private VBox acAndTaskVbox;
    @FXML
    private SegmentedButton segmentedButton;
    @FXML
    private ToggleButton detailsToggleButton;
    @FXML
    private ToggleButton acAndTaskToggleButton;

    @InjectViewModel
    private StoryDetailsPaneViewModel viewModel;

    public void showDetails(final Story story) {
        this.story.set(story);

        segmentedButton.getToggleGroup().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                segmentedButton.getToggleGroup().selectToggle(oldValue);
            } else {
                if (newValue == detailsToggleButton) {
                    showNode(detailsVbox);
                } else if (newValue == acAndTaskToggleButton) {
                    showNode(acAndTaskVbox);
                }
            }


        });

        if (story != null) {

            story.observableTasks().addListener((ListChangeListener<Task>) c -> {
                c.next();
                c.getAddedSubList().forEach(task -> {
                    task.estimateProperty().addListener((observable, oldValue, newValue) -> {
                        // This is a "hack" fix for a JavaFX documented bug.
                        // Extractor is not being called properly when an item is removed from a list and then
                        // added back [as is the case when re-ordering]
                        Task tmpTask = new Task();
                        story.observableTasks().add(tmpTask);
                        story.observableTasks().remove(tmpTask);
                    });
                });
            });
            FxUtils.initListViewFilteredListBiControl(dependenciesLabel, viewModel, viewModel.dependenciesProperty(),
                            viewModel.eligibleDependencies());
            // need to unbind in case the selected story has changed and therefore we won't try and bind to a bound property
            storyScaleLabel.textProperty().unbind();
            storyScaleLabel.textProperty().bind(story.scaleProperty().asString());
            totalHoursLabel.textProperty().unbind();

            totalHoursLabel.textProperty().bind(Bindings.createStringBinding(() -> {
                        float totalHours = 0;
                        for (Task task : story.observableTasks()) {
                            totalHours += task.getEstimate();
                        }

                        return Float.toString(totalHours);

                }, story.observableTasks()
            ));
            setScale();


            if (isReadyListener != null) {
                isReadyCheckBox.selectedProperty().removeListener(isReadyListener);
                story.isReadyProperty().removeListener(modelIsReadyListener);
            }
            isReadyListener = (observable, oldValue, newValue) -> {
                if (story.getIsReady() != newValue) {
                    Command command = new EditCommand<>(story, "isReady", newValue);
                    UndoManager.getUndoManager().doCommand(command);
                }
            };
            modelIsReadyListener = (observable, oldValue, newValue) -> isReadyCheckBox.setSelected(newValue);
            isReadyCheckBox.setSelected(story.getIsReady());
            isReadyCheckBox.selectedProperty().addListener(isReadyListener);
            story.isReadyProperty().addListener(modelIsReadyListener);
        } else {
            totalHoursLabel.setText("0.0");
            storyEstimateSliderLabel.setText(null);
            isReadyCheckBox.selectedProperty().removeListener(isReadyListener);
        }

        acListView.setCellFactory(param -> new AcceptanceCriteriaListCell(acListView, images));
        taskListView.setCellFactory(TaskListCell::new);

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


        // Story must have at least one AC
        // Story must have non-null estimate
        // Story must be in a backlog

        BooleanBinding nullBacklogBinding = Bindings.isNull(story.backlogProperty());
        BooleanBinding emptyACBinding = Bindings.size(story.getAcceptanceCriteria()).isEqualTo(0);
        BooleanBinding noEstimateBinding = Bindings.equal(story.estimateProperty(), 0);

        // Bind the disable property
        isReadyCheckBox.disableProperty().bind(nullBacklogBinding.or(emptyACBinding).or(noEstimateBinding).or(story.inSprintProperty()));
        readyWhy.visibleProperty().bind(isReadyCheckBox.disabledProperty());

        setIsReadyCheckBoxInfo();
        setEstimateHyperlink();

        // Disable storyEstimateSlider if there are no acceptance criteria.
        storyEstimateSlider.disableProperty().bind(Bindings.isEmpty(acListView.getItems()).or(story.inSprintProperty()));

        totalLoggedHours.textProperty().bind(story.spentEffortProperty().asString());

        totalHoursLabel.setOnMouseClicked(event -> {
            story.getTasks().get(0).getObservableLoggedEffort().add(new Effort(new Person(), story.getTasks().get(0), LocalDateTime.now(), Duration.ofMinutes(3), "blah"));
        });
    }

    private void deleteTask() {
        Command command;
        if (taskListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command> commands = new ArrayList<>();
            for (Task task : taskListView.getSelectionModel().getSelectedItems()) {
                commands.add(new DeleteTaskCommand(task, story.get()));
            }
            command = new CompoundCommand("Delete Task", commands);
        } else {
            final Task task = taskListView.getSelectionModel().getSelectedItem();
            command = new DeleteTaskCommand(task, story.get());
        }

        mainController.doCommand(command);
    }

    private void setIsReadyCheckBoxInfo() {
        final Text bulletA = new Text("○ ");
        final Text bulletB = new Text("○ ");
        final Text bulletC = new Text("○ ");

        final Text belongToABacklog = new Text("belong to a backlog\n");
        final Text beEstimated = new Text("be estimated\n");
        final Text haveAcceptanceCriteria = new Text("have Acceptance Criteria");

        belongToABacklog.strikethroughProperty().bind(Bindings.isNotNull(story.get().backlogProperty()));
        beEstimated.strikethroughProperty().bind(Bindings.notEqual(0, story.get().estimateProperty()));
        haveAcceptanceCriteria.strikethroughProperty().bind(Bindings.isNotEmpty(story.get().getAcceptanceCriteria()));

        TextFlow tf = new TextFlow(
                new Text("To mark this Story as Ready, it must:\n\n"),
                bulletA, belongToABacklog,
                bulletB, beEstimated,
                bulletC, haveAcceptanceCriteria);


        tf.setPadding(new Insets(10));
        tf.visibleProperty().bind(story.get().inSprintProperty().not());
        tf.managedProperty().bind(story.get().inSprintProperty().not());

        Text text = new Text("\nThe story is currently allocated to a sprint. ");
        text.visibleProperty().bind(story.get().inSprintProperty());
        text.managedProperty().bind(story.get().inSprintProperty());

        TextFlow tf2 = new TextFlow(tf, text);

        PopOver readyWhyPopOver = new PopOver(tf2);
        readyWhyPopOver.setDetachable(false);

        readyWhy.setOnAction((e) -> readyWhyPopOver.show(readyWhy));
        readyWhy.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide();
            }
        });

        StringProperty readWhyMessage = new SimpleStringProperty("");
        readyWhy.textProperty().bind(readWhyMessage);
        readWhyMessage.bind(Bindings.createStringBinding(() ->
        isReadyCheckBox.isSelected()
                        ? "Why can't I unmark this story as ready?"
                        : "Why can't I mark this story as ready?",
        isReadyCheckBox.selectedProperty()));
    }

    private void deleteAC() {
        Command command;
        if (acListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command> commands = new ArrayList<>();
            for (AcceptanceCriteria ac : acListView.getSelectionModel().getSelectedItems()) {
                commands.add(new DeleteAcceptanceCriteriaCommand(ac, story.get()));
            }
            command = new CompoundCommand("Delete Acceptance Criteria", commands);
        } else {
            final AcceptanceCriteria acceptanceCriteria = acListView.getSelectionModel().getSelectedItem();
            command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story.get());
        }

        // Check if they are deleting all of the ACs, and if the story is marked as ready
        // If so, then the command executed must mark the story as no longer ready
        if (acListView.getSelectionModel().getSelectedItems().size() == acListView.getItems().size() &&
                story.get().getIsReady()) {
            List<Command> changes = new ArrayList<>();
            changes.add(command);
            changes.add(new EditCommand<>(story, "isReady", false));
            command = new CompoundCommand("Delete AC", changes);
        }

        mainController.doCommand(command);
    }

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

        FxUtils.initGoatLabel(longNameLabel, viewModel, viewModel.longNameProperty(), viewModel.longNameValidation());
        FxUtils.initGoatLabel(shortNameLabel, viewModel, viewModel.shortNameProperty(), viewModel.shortNameValidation());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(), viewModel.descriptionValidation());
        FxUtils.initGoatLabel(creatorLabel, viewModel, viewModel.creatorProperty(),
                        StringConverters.personStringConverter(viewModel.organisationProperty()),
                        viewModel.creatorValidation());

        FxUtils.setTextFieldSuggester(creatorLabel.getEditField(), viewModel.creatorSupplier());
        FxUtils.initGoatLabel(priorityLabel, viewModel, viewModel.priorityProperty(), viewModel.priorityValidation());
    }

    private void initSlider() {
        storyEstimateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            // set the sliders label to the estimate string if the value > 0
            if (newValue.intValue() > 0) {
                storyEstimateSliderLabel.setText(story.get().getScale().getEstimates()[(newValue.intValue())-1]);
            } else {
                storyEstimateSliderLabel.setText("-");
            }
            viewModel.estimateProperty().setValue(newValue);
        });

        storyEstimateSlider.setOnMouseReleased(event -> {
            viewModel.commitEdit();
        });
    }

    private void setEstimateHyperlink() {
        Text bulletA = new Text("○ ");
        Text haveAcceptanceCriteria = new Text("have Acceptance Criteria");
        haveAcceptanceCriteria.strikethroughProperty().bind(Bindings.isNotEmpty(story.get().getAcceptanceCriteria()));

        TextFlow tf = new TextFlow(
                new Text("To estimate this Story, it must:\n\n"),
                bulletA, haveAcceptanceCriteria);

        tf.setPadding(new Insets(10));
        tf.visibleProperty().bind(story.get().inSprintProperty().not());
        tf.managedProperty().bind(story.get().inSprintProperty().not());

        Text text = new Text("\nThe story is currently allocated to a sprint. ");
        text.visibleProperty().bind(story.get().inSprintProperty());
        text.managedProperty().bind(story.get().inSprintProperty());

        TextFlow tf2 = new TextFlow(tf, text);


        PopOver estimateWhyPopOver = new PopOver(tf2);
        estimateWhyPopOver.setDetachable(false);




        estimateWhy.setOnAction((e) -> estimateWhyPopOver.show(estimateWhy));
        estimateWhy.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                estimateWhyPopOver.hide();
            }
        });

        estimateWhy.visibleProperty().bind(storyEstimateSlider.disabledProperty());
        estimateWhy.managedProperty().bind(storyEstimateSlider.disabledProperty());

        storyEstimateSlider.visibleProperty().bind(Bindings.not(storyEstimateSlider.disabledProperty().and(story.get().inSprintProperty().not())));
        storyEstimateSlider.managedProperty().bind(Bindings.not(storyEstimateSlider.disabledProperty().and(story.get().inSprintProperty().not())));

        storyEstimateSliderLabel.visibleProperty().bind(Bindings.not(estimateWhy.visibleProperty().and(story.get().inSprintProperty().not())));
        storyEstimateSliderLabel.managedProperty().bind(Bindings.not(estimateWhy.visibleProperty().and(story.get().inSprintProperty().not())));
    }

    private void setScale() {
        if (story.get().getScale() == null) {
            // make slider look tidy
            storyEstimateSlider.setValue(0);
            storyEstimateSlider.setMax(1);
            // bind for if they return to null via revert/undo
            storyEstimateSlider.disableProperty().bind(Bindings.isNull(story.get().scaleProperty()));
        } else {
            // set initial slider value based off story model
            storyEstimateSlider.setValue(story.get().getEstimate());
            storyEstimateSlider.setMax(story.get().getScale().getEstimates().length);
            storyEstimateSlider.disableProperty().unbind();
            storyEstimateSlider.setDisable(false);
            // set initial label for slider, value of 0 is null
            if (storyEstimateSlider.getValue() > 0) {
                storyEstimateSliderLabel.setText(story.get().getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
            }
        }
        // listener for if the scale changes within the story
        story.get().scaleProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null) {
                if (storyEstimateSlider.getValue() > story.get().getScale().getEstimates().length) {
                    // set new value to be new max if slider val > new max
                    storyEstimateSlider.setValue(story.get().getScale().getEstimates().length);
                }
                if (storyEstimateSlider.getValue() > 0) {
                    // if new slider value is greater than 0 (null value for est) set label
                    storyEstimateSliderLabel.setText(story.get().getScale().getEstimates()[(int) storyEstimateSlider.getValue() - 1]);
                }
                storyEstimateSlider.setMax(story.get().getScale().getEstimates().length);
            } else {
                storyEstimateSlider.setValue(0);
                storyEstimateSlider.setMax(1);
            }
        });
        // set the slider to match what the model has (for redo/undo stuff)
        story.get().estimateProperty().addListener((observable, oldValue, newValue) -> {
            storyEstimateSlider.setValue(newValue.intValue());
        });
    }

    private void showNode(Node node) {
        for (Node node1 : mainAnchorPane.getChildren()) {
            node1.setManaged(false);
            node1.setVisible(false);
        }
        node.setManaged(true);
        node.setVisible(true);
    }
}
