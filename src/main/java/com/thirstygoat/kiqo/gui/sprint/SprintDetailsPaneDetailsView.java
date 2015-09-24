package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.StoryTableCell;
import com.thirstygoat.kiqo.gui.customCells.TaskListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelDatePicker;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneView;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneDetailsView implements FxmlView<SprintDetailsPaneDetailsViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneDetailsViewModel viewModel;
    
    @FXML
    private GoatLabelTextField teamLabel;
    @FXML
    private GoatLabelTextField backlogLabel;
    @FXML
    private GoatLabelDatePicker startDateLabel;
    @FXML
    private GoatLabelDatePicker endDateLabel;
    @FXML
    private GoatLabelTextField releaseLabel;
    @FXML
    private GoatLabelTextArea descriptionLabel;
    @FXML
    private TableView<Story> storyTableView;
    @FXML
    private TableColumn<Story, String> shortNameTableColumn;
    @FXML
    private Label totalTaskHoursLabel;
    @FXML
    private ListView<Task> taskListView;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button removeTaskButton;
    @FXML
    private Button editTaskButton;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        FxUtils.initGoatLabel(startDateLabel, viewModel, viewModel.startDateProperty(),
                viewModel.startDateStringProperty(), viewModel.startDateValidation());
        FxUtils.initGoatLabel(endDateLabel, viewModel, viewModel.endDateProperty(), viewModel.endDateStringProperty(),
                viewModel.endDateValidation());
        FxUtils.initGoatLabel(releaseLabel, viewModel, viewModel.releaseProperty(),
                StringConverters.releaseStringConverter(viewModel.organisationProperty()), viewModel.releaseValidation());
        FxUtils.setTextFieldSuggester(releaseLabel.getEditField(), viewModel.releasesSupplier());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());
        FxUtils.initGoatLabel(teamLabel, viewModel, viewModel.teamProperty(),
                StringConverters.teamStringConverter(viewModel.organisationProperty()), viewModel.teamValidation());
        FxUtils.setTextFieldSuggester(teamLabel.getEditField(), viewModel.teamsSupplier());
        FxUtils.initGoatLabel(backlogLabel, viewModel, viewModel.backlogProperty(),
                StringConverters.backlogStringConverter(viewModel.organisationProperty()), viewModel.backlogValidation());
        FxUtils.setTextFieldSuggester(backlogLabel.getEditField(), viewModel.backlogsSupplier());
        FxUtils.initGoatLabel(descriptionLabel, viewModel, viewModel.descriptionProperty(),
                viewModel.descriptionValidation());

        storyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        storyTableView.itemsProperty().bind(viewModel.stories());

        placeHolder.textProperty().set(SprintDetailsPaneDetailsViewModel.PLACEHOLDER);

        shortNameTableColumn.setCellFactory((s) ->  {
            StoryTableCell storyTableCell = new StoryTableCell(viewModel);
            storyTableCell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getClickCount() > 1) {
                    MainController.focusedItemProperty.set((Story) storyTableCell.getTableRow().getItem());
                }
            });
            return storyTableCell;
        });

        Callable<String> taskHoursCallable = () -> {
            float totalHours = 0;
            for (Task task : viewModel.tasksWithoutStoryProperty().get().observableTasks()) {
                totalHours += task.getEstimate();
            }

            return Float.toString(totalHours);
        };

        viewModel.tasksWithoutStoryProperty().addListener((observable, oldValue, newValue) -> {
            totalTaskHoursLabel.textProperty().unbind();
            totalTaskHoursLabel.textProperty().bind(Bindings.createStringBinding(
                    taskHoursCallable, viewModel.tasksWithoutStoryProperty().get().observableTasks()
            ));

        });

        // A StoryDetailsPaneView is constructed here, since the TaskListCell requires one.
        // The use of it is for storage of the fields relating to the currently
        // dragging task
        StoryDetailsPaneView taskCellView = new StoryDetailsPaneView();
        taskListView.setCellFactory(param -> new TaskListCell(taskListView, taskCellView));
        removeTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));

        addTaskButton.setOnAction(event -> viewModel.createTask());
        removeTaskButton.setOnAction(event -> viewModel.deleteTasks(taskListView.getSelectionModel().getSelectedItems()));
        editTaskButton.setOnAction(event -> viewModel.editTask(taskListView.getSelectionModel().getSelectedItem()));

        viewModel.tasksWithoutStoryProperty().addListener((observable, oldValue, newValue) ->{
            taskListView.setItems(newValue.observableTasks());
        });
    }

    public SprintDetailsPaneDetailsViewModel getViewModel() {
        return viewModel;
    }
}
