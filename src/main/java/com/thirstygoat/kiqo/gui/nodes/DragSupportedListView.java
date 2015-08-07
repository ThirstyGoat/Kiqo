package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * Created by bradley on 7/08/15.
 */
public class DragSupportedListView<T> extends ListView<T> {
    private ListCell<T> currentlyDraggingCell;

    public ListCell<T> getCurrentlyDraggingCell() {
        return currentlyDraggingCell;
    }

    public void setCurrentlyDraggingCell(ListCell<T> cell) {
        currentlyDraggingCell = cell;
    }
}
