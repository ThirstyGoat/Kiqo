package com.thirstygoat.kiqo.gui.customCells;


import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.DragContainer;
import com.thirstygoat.kiqo.gui.story.StoryDetailsPaneView;
import com.thirstygoat.kiqo.model.Status;
import com.thirstygoat.kiqo.model.Task;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class TaskListCell extends ListCell<Task> {
    private ListView<Task> listView;
    private UndoManager undoManager = UndoManager.getUndoManager();

    public TaskListCell(ListView<Task> listView) {
        this.listView = listView;
    }


    @Override
    protected void updateItem(final Task task, final boolean empty) {
        if (!empty) {
            initialiseDragAndDrop(task);
            
            final HBox hBox = new HBox();

            Text name = new Text();
            name.textProperty().bind(task.shortNameProperty());
            name.setStyle("-fx-font: 13px \"System\";");
//            name.setWrappingWidth(listView.getWidth() * 0.65);

            Text description = new Text();
            description.textProperty().bind(task.descriptionProperty());
            description.setStyle("-fx-font: 9px \"System\";");
            description.wrappingWidthProperty().bind(listView.widthProperty().subtract(250));
//            description.setWrappingWidth(listView.getWidth() * 0.65);

            final ComboBox<Status> statusComboBox = new ComboBox<>();
            statusComboBox.setItems(FXCollections.observableArrayList(Status.values()));
            statusComboBox.setValue(task.getStatus());
            
            statusComboBox.setButtonCell(new ListCell<Status>() {
            	@Override
            	public void updateItem(Status item, boolean empty) {
            		{
            			setTextFill(Color.WHITE);
            		}
            		String cssClass = null;
            		super.updateItem(item, empty);
            		if (!empty) {
            			getStyleClass().remove(cssClass);
            			cssClass = item.getCssClass();
            			getStyleClass().add(cssClass);
            			setText(item.toString());
            		}
            	}
            });
            statusComboBox.setStyle("-fx-font: 10px \"System\"; -fx-border-width: 0.5px; -fx-border-color: black;");
            
            statusComboBox.setMaxWidth(100);


            statusComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != task.getStatus()) {
                    Command command = new EditCommand<>(task, "status", newValue);
                    UndoManager.getUndoManager().doCommand(command);
                }

                statusComboBox.getButtonCell().getStyleClass().remove(oldValue.getCssClass());
                statusComboBox.getButtonCell().getStyleClass().add(newValue.getCssClass());
            });
            task.statusProperty().addListener((observable, oldValue, newValue) -> {
            	if (newValue != statusComboBox.getValue()) {
            		statusComboBox.valueProperty().set(newValue);
            	}
            });
            
            ToggleButton blockedButton = new ToggleButton();
            blockedButton.selectedProperty().bindBidirectional(task.blockedProperty());
            blockedButton.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BAN));
            blockedButton.getStyleClass().add("blocked-button");



            Text effortText = new Text();
            Text estimateText = new Text();
            TextFlow timeTextFlow = new TextFlow(effortText, new Text("/"), estimateText);
            timeTextFlow.setTextAlignment(TextAlignment.RIGHT);

            effortText.textProperty().bind(task.spentEffortProperty().asString());
            estimateText.textProperty().bind(task.estimateProperty().asString());

            Label estimate = new Label();
            estimate.setPadding(new Insets(0, 10, 0, 0));
            estimate.setMaxHeight(20);
            estimate.setAlignment(Pos.TOP_RIGHT);
            estimate.setGraphic(timeTextFlow);
            VBox vBox = new VBox();
            vBox.getChildren().addAll(name, description);

            HBox.setHgrow(vBox, Priority.ALWAYS);
            hBox.getChildren().addAll(vBox, estimate, statusComboBox, blockedButton);

            setGraphic(hBox);
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
