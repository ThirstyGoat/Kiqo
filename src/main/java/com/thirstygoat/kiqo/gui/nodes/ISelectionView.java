package com.thirstygoat.kiqo.gui.nodes;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public interface ISelectionView<T> {

    /**
     * @return ListView displaying the pool of available items
     */
    @Deprecated
    public default ListView<T> getSourceListView() { return null; }

    /**
     * @return ListView displaying the assigned items
     */
    @Deprecated
    public default ListView<T> getTargetListView() { return null; }

    /**
     * Sets a new cell factory to use by both list views. This forces all old List Cells to be thrown away, and new
     * ListCells created with the new cell factory.
     *
     * @param cellFactory Cell Factory for both ListViews
     */
    @Deprecated
    public default void setCellFactories(Callback<ListView<T>, ListCell<T>> cellFactory) {};

    public ObjectProperty<ObservableList<T>> getSourceItemsProperty();
    
    public ObjectProperty<ObservableList<T>> getTargetItemsProperty();

}
