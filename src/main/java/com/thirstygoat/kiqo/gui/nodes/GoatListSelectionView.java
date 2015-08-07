package com.thirstygoat.kiqo.gui.nodes;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.controlsfx.control.ListSelectionView;

/**
 * Created by bradley on 26/03/15. Extends original ListSelectionView as authored in the ControlsFX library This class
 * allows a custom cell factory which is great.
 *
 * @param <T> Type of item to be displayed in the listviews
 */
public class GoatListSelectionView<T> extends ListSelectionView<T> implements ISelectionView<T> {
    public final GoatListSelectionViewSkin<T> skin;
    private ListView<T> sourceListView;
    private ListView<T> targetListView;

    /**
     * Constructor.
     */
    public GoatListSelectionView() {
        super();

        skin = new GoatListSelectionViewSkin<T>(this) {
            {
                sourceListView = getSourceListView();
                targetListView = getTargetListView();
            }
        };

        setSkin(skin);
    }

    /**
     * @return ListView displaying the pool of available items
     */
    @Deprecated
    @Override
    public ListView<T> getSourceListView() {
        return sourceListView;
    }

    /**
     * @return ListView displaying the assigned items
     */
    @Deprecated
    @Override
    public ListView<T> getTargetListView() {
        return targetListView;
    }

    /**
     * Sets a new cell factory to use by both list views. This forces all old List Cells to be thrown away, and new
     * ListCells created with the new cell factory.
     *
     * @param cellFactory Cell Factory for both ListViews
     * @deprecated
     */
    @Deprecated
    @Override
    public final void setCellFactories(Callback<ListView<T>, ListCell<T>> cellFactory) {
        sourceListView.setCellFactory(cellFactory);
        targetListView.setCellFactory(cellFactory);
    }

    @Override
    public ObjectProperty<ObservableList<T>> getSourceItemsProperty() {
        return sourceListView.itemsProperty();
    }

    @Override
    public ObjectProperty<ObservableList<T>> getTargetItemsProperty() {
        return targetListView.itemsProperty();
    }
}
