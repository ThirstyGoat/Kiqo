package com.thirstygoat.kiqo.gui.customCells;


import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.gui.DragContainer;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneView;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Task;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Cursor;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

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
                    List<Command> changes = new ArrayList<>();
                    changes.add(new EditCommand<>(task, "status", newValue));
                    if (newValue == Status.DONE)
                        changes.add(new EditCommand<>(task, "blocked", false));
                    UndoManager.getUndoManager().doCommand(new CompoundCommand("Edit Task", changes));
                }
            });

            task.statusProperty().addListener((observable, oldValue, newValue) -> {
                statusComboBox.valueProperty().set(newValue);
                statusComboBox.setStyle(statusComboBox.getStyle() + "-fx-background-color: #" + task.getStatus().color.toString().substring(2) + ";");
            });
            statusComboBox.setStyle(statusComboBox.getStyle() + "-fx-background-color: #" + task.getStatus().color.toString().substring(2) + ";");

            ToggleButton blockedButton = new ToggleButton();
            blockedButton.selectedProperty().bindBidirectional(task.blockedProperty());
            blockedButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BAN));
            blockedButton.getStyleClass().add("blocked-button");


            Text estimate = new Text();
            estimate.textProperty().bind(task.estimateProperty().asString());


            gridPane.add(name, 0, 0);
            gridPane.add(description, 0, 1);
            gridPane.add(statusComboBox, 1, 0);
            gridPane.add(blockedButton, 2, 0);
            gridPane.add(estimate, 3, 0);
            GridPane.setRowSpan(statusComboBox, 2);
            GridPane.setRowSpan(estimate, 1);

            ColumnConstraints column1 = new ColumnConstraints();
            ColumnConstraints column2 = new ColumnConstraints();
            ColumnConstraints column3 = new ColumnConstraints();
            ColumnConstraints column4 = new ColumnConstraints();
            column1.setPercentWidth(65);
            column2.setPercentWidth(20);
            column3.setPercentWidth(10);
            column4.setPercentWidth(5);
            column2.setHalignment(HPos.LEFT);
            column3.setHalignment(HPos.LEFT);
            column4.setHalignment(HPos.LEFT);
            gridPane.getColumnConstraints().addAll(column1, column2, column3, column4);

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
            if (StoryDetailsPaneView.draggingTask != null) {
                ((TaskListCell) event.getSource()).setStyle("-fx-background-color: greenyellow");
                event.acceptTransferModes(TransferMode.ANY);
                Task t = StoryDetailsPaneView.draggingTask;
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
            if (StoryDetailsPaneView.draggingTask != null) {
                ((TaskListCell) event.getSource()).setStyle(null);
                event.acceptTransferModes(TransferMode.ANY);
                Task t = StoryDetailsPaneView.draggingTask;
                listView.getItems().remove(t);
            }
            event.consume();
        };

        // Called when the item is dropped
        EventHandler<DragEvent> mContextDragDropped = event -> {
            if (StoryDetailsPaneView.draggingTask != null) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                Task t = StoryDetailsPaneView.draggingTask;
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
            // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
            if (StoryDetailsPaneView.draggingTask != null) {
                Task t = StoryDetailsPaneView.draggingTask;
                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");

                if (!listView.getItems().contains(t)) {
                    listView.getItems().add(prevIndex, t);
                }
                StoryDetailsPaneView.draggingTask = null;
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

            StoryDetailsPaneView.draggingTask = task;

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
