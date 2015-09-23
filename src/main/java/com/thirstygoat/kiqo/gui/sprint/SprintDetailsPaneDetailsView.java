package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.delete.DeleteTaskCommand;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.gui.customCells.StoryTableCell;
import com.thirstygoat.kiqo.gui.customCells.TaskListCell;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelDatePicker;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextArea;
import com.thirstygoat.kiqo.gui.nodes.GoatLabelTextField;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneDetailsView implements FxmlView<SprintDetailsPaneDetailsViewModel>, Initializable {
    
    private Label placeHolder = new Label();
    
    @InjectViewModel
    private SprintDetailsPaneDetailsViewModel viewModel;

    private MainController mainController;
    
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
                viewModel.descriptionValidation(), "Add a description...");
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

        taskListView.setCellFactory(TaskListCell::new);
        removeTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editTaskButton.disableProperty().bind(Bindings.size(taskListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        addTaskButton.setOnAction(event -> mainController.createTask());
        removeTaskButton.setOnAction(event -> deleteTask());
        editTaskButton.setOnAction(event -> mainController.editTask(taskListView.getSelectionModel().getSelectedItem()));

        viewModel.tasksWithoutStoryProperty().addListener((observable, oldValue, newValue) ->{
            taskListView.setItems(newValue.observableTasks());
        });

    }

    private void deleteTask() {
        Command command;
        if (taskListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command> commands = new ArrayList<>();
            for (Task task : taskListView.getSelectionModel().getSelectedItems()) {

                commands.add(new DeleteTaskCommand(task, viewModel.tasksWithoutStoryProperty().get()));
            }
            command = new CompoundCommand("Delete Task", commands);
        } else {
            final Task task = taskListView.getSelectionModel().getSelectedItem();
            command = new DeleteTaskCommand(task, viewModel.tasksWithoutStoryProperty().get());
        }

        mainController.doCommand(command);
    }

    public SprintDetailsPaneDetailsViewModel getViewModel() {
        return viewModel;
    }
}
