package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;

/**
 * Created by Bradley on 7/08/15.
 */
public class ReorderableListView<T extends Item> extends ListView<T> {
    private ListCell<T> currentlyDraggingListCell;

    public ReorderableListView() {
        super();
        initializeDragAndDrop();
    }

    public ReorderableListView(ObservableList<T> items) {
        super(items);
        initializeDragAndDrop();
    }

    public ListCell<T> getCurrentlyDraggingListCell() {
        return currentlyDraggingListCell;
    }

    public void setCurrentlyDraggingListCell(ListCell<T> currentlyDraggingListCell) {
        this.currentlyDraggingListCell = currentlyDraggingListCell;
    }

    private void moveNode(ListCell<T> cell, int index) {
        getItems().remove(cell.getItem());
        getItems().add(index, cell.getItem());
    }

    public void initializeDragAndDrop() {
        this.setOnDragOver(event -> {
            System.out.println(getChildren());
//            if (currentlyDraggingListCell != null) {
//                this.getItems().remove(currentlyDraggingListCell.getItem());
//                event.acceptTransferModes(TransferMode.MOVE);
//                event.consume();
//
//                Node closestNode = null;
//                double closestNodePosition = Double.MAX_VALUE;
//                double cursorPosition = event.getY();
//
//                for (Node node : getChildren()) {
//                    // If cursor position falls within x bounds of node then check if should appear to left or right of node
//                    double thisNodeY = node.localToScene(Point2D.ZERO).getY();
//                    if (Math.abs(thisNodeY - cursorPosition) < closestNodePosition) {
//                        closestNode = node;
//                        closestNodePosition = Math.abs(thisNodeY - cursorPosition);
//                    }
//                }
//                if (closestNode != null && cursorPosition <= closestNode.localToScene(Point2D.ZERO).getY()) {
//                    // Then the dragged node should appear to left of closest node
//                    moveNode(currentlyDraggingListCell, getChildren().indexOf(closestNode));
//                } else {
//                    // Then the dragged node should appear to right of closest node
//                    moveNode(currentlyDraggingListCell, getChildren().indexOf(closestNode) + 1);
//                }
//            }
        });
    }
}
