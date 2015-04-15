package seng302.group4.customNodes;

import impl.org.controlsfx.skin.ListSelectionViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import org.controlsfx.control.ListSelectionView;

/**
 * Created by bradley on 26/03/15.
 * Extends original ListSelectionView as authored in the ControlsFX library
 * This class allows a custom cell factory which is great.
 */
public class GoatListSelectionView<T> extends ListSelectionView<T> {
    private ListView<T> sourceListView;
    private ListView<T> targetListView;
    // --- Cell Factory
    private ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory;

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

    public ListView<T> getSourceListView() {
        return sourceListView;
    }

    public ListView<T> getTargetListView() {
        return targetListView;
    }

    /**
     * Sets a new cell factory to use by both list views. This forces all old
     * List Cells to be thrown away, and new ListCells created with the new cell
     * factory.
     * 
     * @param cellFactory
     */
    public final void setCellFactories(Callback<ListView<T>, ListCell<T>> cellFactory) {
        sourceListView.setCellFactory(cellFactory);
        targetListView.setCellFactory(cellFactory);
    }
}
