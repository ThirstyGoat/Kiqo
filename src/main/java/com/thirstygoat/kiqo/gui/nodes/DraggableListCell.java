package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.DragContainer;
import com.thirstygoat.kiqo.model.Item;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.ListCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 * Created by bradley on 7/08/15.
 */
public class DraggableListCell<T extends Item> extends ListCell<T> {
    private ReorderableListView<T> listView;

    public DraggableListCell(ReorderableListView<T> listView) {
        this.listView = listView;
        initializeDragAndDrop();
    }

    private void initializeDragAndDrop() {
        this.setOnDragDetected(event -> {
            getReorderableListView().setCurrentlyDraggingListCell(this);
            setCursor(Cursor.CLOSED_HAND);
            Dragboard dragboard = listView.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(""); // Empty string on the clipboard since we need to transfer something
            dragboard.setContent(cc);
            event.consume();
            System.out.println("drag detected on item: " + getItem());
        });
    }

    private void initialiseDragAndDrop() {
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
            if (getReorderableListView().getCurrentlyDraggingListCell() != null) {
                this.setStyle("-fx-background-color: greenyellow");
                event.acceptTransferModes(TransferMode.ANY);

                T draggingItem = getReorderableListView().getCurrentlyDraggingListCell().getItem();

                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                if (getIndex() < listSize) {
                    listView.getItems().add(getIndex(), draggingItem);
                } else {
                    listView.getItems().add(draggingItem);
                }
            }
            event.consume();
        };

        // Called when the dragged item leaves another cell
        EventHandler<DragEvent> mContextDragExit = event -> {
            if (getReorderableListView().getCurrentlyDraggingListCell() != null) {

                this.setStyle(null);
                event.acceptTransferModes(TransferMode.ANY);

                T draggingItem = getReorderableListView().getCurrentlyDraggingListCell().getItem();
                listView.getItems().remove(draggingItem);
            }
            event.consume();
        };

        // Called when the item is dropped
        EventHandler<DragEvent> mContextDragDropped = event -> {
            if (getReorderableListView().getCurrentlyDraggingListCell() != null) {
                getParent().setOnDragOver(null);
                getParent().setOnDragDropped(null);
                T draggingItem = getReorderableListView().getCurrentlyDraggingListCell().getItem();
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");
                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                if (getIndex() < listSize) {
                    if (prevIndex != getIndex()) {
                        UndoManager.getUndoManager().doCommand(new MoveItemCommand<>(draggingItem, listView.getItems(), prevIndex, listView.getItems(), getIndex()));
                    }
                } else {
                    UndoManager.getUndoManager().doCommand(new MoveItemCommand<>(draggingItem, listView.getItems(), prevIndex,
                            listView.getItems(), listView.getItems().size() - 1));
                }
                event.setDropCompleted(true);
            }
            event.consume();
        };

        // Called when the drag and drop is complete
        EventHandler<DragEvent> mContextDragDone = event -> {
            // When the drag and drop is done, check if it is in the list, if it isn't put it back at its old position
            if (getReorderableListView().getCurrentlyDraggingListCell() != null) {
//            System.out.println("done");
                T draggingItem = getReorderableListView().getCurrentlyDraggingListCell().getItem();

                int prevIndex = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("index");
                int listSize = ((DragContainer) event.getDragboard().getContent(DragContainer.DATA_FORMAT)).getValue("listSize");

                if (listSize > listView.getItems().size()) {
                    listView.getItems().add(prevIndex, draggingItem);
                }
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

            // Set currently dragging item
            getReorderableListView().setCurrentlyDraggingListCell(this);

            // begin drag ops
            ClipboardContent content = new ClipboardContent();
            DragContainer container = new DragContainer();
            container.addData("type", "AC");
            container.addData("listSize", listView.getItems().size());
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

    private ReorderableListView<T> getReorderableListView() {
        return listView;
    }
}
