package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.nodes.TaskCard;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Task;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by bradley on 14/08/15.
 */
public class StoryRowView implements FxmlView<StoryRowViewModel>, Initializable {

    @InjectViewModel
    private StoryRowViewModel viewModel;

    private TaskCard currentlyDraggingTaskCard;

    @FXML
    private GridPane gridPane;
    @FXML
    private Label storyNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label estimateLabel;
    @FXML
    private Button addTaskButton;
    @FXML
    private FlowPane toDoTasks;
    @FXML
    private FlowPane inProgressTasks;
    @FXML
    private FlowPane verifyTasks;
    @FXML
    private FlowPane doneTasks;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        storyNameLabel.textProperty().bind(viewModel.storyNameProperty());
        descriptionLabel.textProperty().bind(viewModel.descriptionProperty());
        descriptionLabel.managedProperty().bind(Bindings.createBooleanBinding(() -> {
            return descriptionLabel.getText() == null || !descriptionLabel.getText().isEmpty();
        }, descriptionLabel.textProperty()));
        priorityLabel.textProperty().bind(viewModel.priorityProperty().asString());
        estimateLabel.textProperty().bind(viewModel.estimateProperty());

        initialiseDragAndDrop();
        drawTasks();
        setAddTaskButton();
    }

    private void setAddTaskButton() {
        addTaskButton.setOnAction(event -> {
            // Open new task window for this story
            viewModel.newTask();
        });
    }

    private void drawTasks() {
        Function<Task, TaskCard> fn = task -> {
            TaskCard tc = new TaskCard(task);
            tc.getStyleClass().add(task.getStatus().getCssClass());
//            tc.setCursor(Cursor.OPEN_HAND);
            addDragHandler(tc);
            return tc;
        };

        toDoTasks.getChildren().setAll(viewModel.getToDoTasks().stream().map(fn).collect(Collectors.toList()));
        inProgressTasks.getChildren().setAll(viewModel.getInProgressTasks().stream().map(fn).collect(Collectors.toList()));
        verifyTasks.getChildren().setAll(viewModel.getVerifyTasks().stream().map(fn).collect(Collectors.toList()));
        doneTasks.getChildren().setAll(viewModel.getDoneTasks().stream().map(fn).collect(Collectors.toList()));

        ListChangeListener<Task> listener = c -> {
            toDoTasks.getChildren().setAll(viewModel.getToDoTasks().stream().map(fn).collect(Collectors.toList()));
            inProgressTasks.getChildren().setAll(viewModel.getInProgressTasks().stream().map(fn).collect(Collectors.toList()));
            verifyTasks.getChildren().setAll(viewModel.getVerifyTasks().stream().map(fn).collect(Collectors.toList()));
            doneTasks.getChildren().setAll(viewModel.getDoneTasks().stream().map(fn).collect(Collectors.toList()));
        };

        viewModel.getToDoTasks().addListener(listener);
        viewModel.getInProgressTasks().addListener(listener);
        viewModel.getVerifyTasks().addListener(listener);
        viewModel.getDoneTasks().addListener(listener);
    }

    private void addDragHandler(TaskCard node) {
        node.setOnDragDetected(event -> {
            currentlyDraggingTaskCard = node;
            node.setCursor(Cursor.CLOSED_HAND);
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent cc = new ClipboardContent();
            cc.putString(""); // Empty string on the clipboard since we need to transfer something
            db.setContent(cc);

            ImageView dragImage = new ImageView(node.snapshot(null, null));
            db.setDragView(dragImage.getImage());
            event.consume();
        });

        node.setOnDragDone(event -> {
            currentlyDraggingTaskCard.setCursor(Cursor.OPEN_HAND);
            currentlyDraggingTaskCard = null;
        });
    }

    private void initialiseDragAndDrop() {
        gridPane.setOnDragDetected(event -> {
            ScrumBoardView.currentlyDraggingStoryRow = gridPane;
            Dragboard dragboard = gridPane.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent cc = new ClipboardContent();
            cc.putString(""); // Empty string on the clipboard since we need to transfer something
            dragboard.setContent(cc);

            ImageView dragImage = new ImageView(gridPane.snapshot(null, null));
            dragboard.setDragView(dragImage.getImage());
        });

        EventHandler<DragEvent> dragOver = event -> {
            if (currentlyDraggingTaskCard != null) {
                event.acceptTransferModes(TransferMode.MOVE);
                ((Node)event.getSource()).setStyle("-fx-background-color: #E0E0E0");
            }
        };

        toDoTasks.setOnDragOver(dragOver);
        inProgressTasks.setOnDragOver(dragOver);
        verifyTasks.setOnDragOver(dragOver);
        doneTasks.setOnDragOver(dragOver);

        toDoTasks.setOnDragDropped(event -> {
            if (currentlyDraggingTaskCard != null) {
                Status currentStatus = currentlyDraggingTaskCard.getTask().getStatus();
                if (currentStatus != Status.NOT_STARTED) {
                    Command moveCommand = new EditCommand<>(currentlyDraggingTaskCard.getTask(), "status", Status.NOT_STARTED);
                    UndoManager.getUndoManager().doCommand(moveCommand);
                }
            }
        });

        inProgressTasks.setOnDragDropped(event -> {
            if (currentlyDraggingTaskCard != null) {
                Status currentStatus = currentlyDraggingTaskCard.getTask().getStatus();
                if (currentStatus != Status.IN_PROGRESS) {
                    Command moveCommand = new EditCommand<>(currentlyDraggingTaskCard.getTask(), "status", Status.IN_PROGRESS);
                    UndoManager.getUndoManager().doCommand(moveCommand);
                }
            }
        });

        verifyTasks.setOnDragDropped(event -> {
            if (currentlyDraggingTaskCard != null) {
                Status currentStatus = currentlyDraggingTaskCard.getTask().getStatus();
                if (currentStatus != Status.VERIFY) {
                    Command moveCommand = new EditCommand<>(currentlyDraggingTaskCard.getTask(), "status", Status.VERIFY);
                    UndoManager.getUndoManager().doCommand(moveCommand);
                }
            }
        });

        doneTasks.setOnDragDropped(event -> {
            if (currentlyDraggingTaskCard != null) {
                Status currentStatus = currentlyDraggingTaskCard.getTask().getStatus();
                if (currentStatus != Status.DONE) {
                    Command moveCommand = new EditCommand<>(currentlyDraggingTaskCard.getTask(), "status", Status.DONE);
                    UndoManager.getUndoManager().doCommand(moveCommand);
                }
            }
        });

        EventHandler<DragEvent> dragExitEvent = event -> {
            ((Node)event.getSource()).setStyle("-fx-background-color: transparent");
        };

        toDoTasks.setOnDragExited(dragExitEvent);
        inProgressTasks.setOnDragExited(dragExitEvent);
        verifyTasks.setOnDragExited(dragExitEvent);
        doneTasks.setOnDragExited(dragExitEvent);
    }
}
