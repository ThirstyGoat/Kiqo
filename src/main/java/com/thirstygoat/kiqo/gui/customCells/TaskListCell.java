package com.thirstygoat.kiqo.gui.customCells;


import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.DragContainer;
import com.thirstygoat.kiqo.gui.detailsPane.StoryDetailsPaneController;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Task;

public class TaskListCell extends ListCell<Task> {
    private ListView<Task> listView;
    private UndoManager undoManager = UndoManager.getUndoManager();

    public TaskListCell(ListView<Task> listView) {
        this.listView = listView;
    }


    @Override
    protected void updateItem(final Task task, final boolean empty) {
        // calling super here is very important
        if (!empty) {
            initialiseDragAndDrop(task);
            
            final GridPane gridPane = new GridPane();
            Text name = new Text();
            name.textProperty().bind(task.shortNameProperty());
            name.setStyle("-fx-font: 13px \"System\";");
            name.setWrappingWidth(listView.getWidth() * 0.65);

            Text description = new Text();
            description.textProperty().bind(task.descriptionProperty());
            description.setStyle("-fx-font: 9px \"System\";");
            description.setWrappingWidth(listView.getWidth() * 0.65);

            final ComboBox<Status> statusComboBox = new ComboBox<>();
            statusComboBox.setStyle("-fx-font: 10px \"System\"; -fx-border-width: 0.5px; -fx-border-color: black;");
            statusComboBox.setMaxWidth(90);

            statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));
            statusComboBox.setValue(task.getStatus());

            statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != task.getStatus()) {
                    Command command = new EditCommand<>(task, "status", newValue);
                    UndoManager.getUndoManager().doCommand(command);
                }
            });

            task.statusProperty().addListener((observable, oldValue, newValue) -> {
                statusComboBox.valueProperty().set(newValue);
                statusComboBox.setStyle(statusComboBox.getStyle() + "-fx-background-color: #" + task.getStatus().color.toString().substring(2) + ";");
            });
            statusComboBox.setStyle(statusComboBox.getStyle() + "-fx-background-color: #" + task.getStatus().color.toString().substring(2) + ";");


            Text estimate = new Text();
            estimate.textProperty().bind(task.estimateProperty().asString());

            gridPane.add(name,0, 0);
            gridPane.add(description, 0, 1);
            gridPane.add(statusComboBox, 1, 0);
            gridPane.add(estimate, 2, 0);
            GridPane.setRowSpan(statusComboBox, 2);
            GridPane.setRowSpan(estimate, 2);

            ColumnConstraints column1 = new ColumnConstraints();
            ColumnConstraints column2 = new ColumnConstraints();
            ColumnConstraints column3 = new ColumnConstraints();
            column1.setPercentWidth(70);
            column2.setPercentWidth(25);
            column3.setPercentWidth(5);
            column3.setHalignment(HPos.RIGHT);
            gridPane.getColumnConstraints().addAll(column1, column2, column3);

            setGraphic(gridPane);
        } else {
            // clear
            setGraphic(null);
        }
        super.updateItem(task, empty);
    }
    
    private void initialiseDragAndDrop(Task task) {

        // Called when the dragged item is over another cell
        EventHandler<DragEvent> mContextDragOver = event -> {
            event.acceptTransferModes(TransferMode.ANY);
            int buffer = 20;
            double yPos = getParent().sceneToLocal(event.getSceneX(), event.getSceneY()).getY();
            if (yPos < buffer) {
                listView.scrollTo(getIndex() - 2);
            } else if (yPos > listView.getHeight() - buffer) {
                listView.scrollTo(getIndex() - 3);
            }
            event.consume();
        };

        // Called when the dragged item enters another cell
        EventHandler<DragEvent> mContextDragEntered = event -> {
//            System.out.println("Enter");
            if (StoryDetailsPaneController.draggingTask != null) {
                ((TaskListCell) event.getSource()).setStyle("-fx-background-color: greenyellow");
                event.acceptTransferModes(TransferMode.ANY);
                Task t = StoryDetailsPaneController.draggingTask;
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (getIndex() < listSize) {
                    listView.getItems().add(getIndex(), t);
                } else {
                    listView.getItems().add(t);
                }
            }
            event.consume();
        };

        // Called when the dragged item leaves another cell
        EventHandler<DragEvent> mContextDragExit = event -> {
            if (StoryDetailsPaneController.draggingTask != null) {
                ((TaskListCell) event.getSource()).setStyle(null);
                event.acceptTransferModes(TransferMode.ANY);
                Task t = StoryDetailsPaneController.draggingTask;
                listView.getItems().remove(t);
            }
            event.consume();
        };

        // Called when the item is dropped
        EventHandler<DragEvent> mContextDragDropped = event -> {
            if (StoryDetailsPaneController.draggingTask != null) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                Task t = StoryDetailsPaneController.draggingTask;
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                if (getIndex() < listSize) {
                    if (prevIndex != getIndex()) {
                        undoManager.doCommand(new MoveItemCommand<>(task, task.getStory().observableTasks(), prevIndex, task.getStory().observableTasks(), getIndex()));
                    }
                } else {
                    if (!listView.getItems().contains(t)) {
                        undoManager.doCommand(new MoveItemCommand<>(task, listView.getItems(), prevIndex,
                                listView.getItems(), listView.getItems().size() - 1));
                    }
                }
                event.setDropCompleted(true);
            }
            event.consume();
        };

        // Called when the drag and drop is complete
        EventHandler<DragEvent> mContextDragDone = event -> {
//            System.out.println("done");
            // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
            if (StoryDetailsPaneController.draggingTask != null) {
                Task t = StoryDetailsPaneController.draggingTask;
                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");

                if (!listView.getItems().contains(t)) {
                    listView.getItems().add(prevIndex, t);
                }
                StoryDetailsPaneController.draggingTask = null;
            }
            event.consume();
        };

        this.setOnDragDropped(mContextDragDropped);
        this.setOnDragOver(mContextDragOver);
        this.setOnDragEntered(mContextDragEntered);
        this.setOnDragExited(mContextDragExit);
        setCursor(Cursor.HAND);

        this.setOnDragDetected(event -> {
            // We do need this one or onDragDone wont be called
            setCursor(Cursor.CLOSED_HAND);
            getParent().setOnDragDone(mContextDragDone);

            StoryDetailsPaneController.draggingTask = task;

            // begin drag ops
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();
            container.addData("name", task.getShortName());
            container.addData("description", task.getDescription());
            container.addData("status", task.getStatus());
            container.addData("estimate", task.getEstimate());
            container.addData("listSize", listView.getItems().size());
            container.addData("type", "TASK");
            content.put(DragContainer.DATA_FORMAT, container);

            if (getIndex() == listView.getSelectionModel().getSelectedIndex()) {
                container.addData("index", listView.getSelectionModel().getSelectedIndex());
                listView.getItems().remove(getIndex());
                listView.getSelectionModel().clearSelection();
                getParent().startDragAndDrop(TransferMode.MOVE).setContent(content);
            }
            event.consume();
        });
    }


}
