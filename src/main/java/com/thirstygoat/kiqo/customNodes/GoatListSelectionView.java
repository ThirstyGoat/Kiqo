package com.thirstygoat.kiqo.customNodes;

import impl.org.controlsfx.skin.ListSelectionViewSkin;
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
public class GoatListSelectionView<T> extends ListSelectionView<T> {
    private ListView<T> sourceListView;
    private ListView<T> targetListView;

    /**
     * Constructor.
     */
    public GoatListSelectionView() {
        super();

        final ListSelectionViewSkin<T> skin = new ListSelectionViewSkin<T>(this) {
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
    public ListView<T> getSourceListView() {
        return sourceListView;
    }

    /**
     * @return ListView displaying the assigned items
     */
    public ListView<T> getTargetListView() {
        return targetListView;
    }

    /**
     * Sets a new cell factory to use by both list views. This forces all old List Cells to be thrown away, and new
     * ListCells created with the new cell factory.
     *
     * @param cellFactory Cell Factory for both ListViews
     */
    public final void setCellFactories(Callback<ListView<T>, ListCell<T>> cellFactory) {
        sourceListView.setCellFactory(cellFactory);
        targetListView.setCellFactory(cellFactory);
    }
}
