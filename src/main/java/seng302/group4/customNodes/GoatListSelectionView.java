package seng302.group4.customNodes;

import impl.org.controlsfx.skin.ListSelectionViewSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

        ListSelectionViewSkin<T> skin = new ListSelectionViewSkin<T>(this) {
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
     * Returns the current cell factory.
     */
    public final Callback<ListView<T>, ListCell<T>> getCellFactory() {
        return cellFactory == null ? null : cellFactory.get();
    }

    /**
     * Sets a new cell factory to use by both list views. This forces all old
     * List Cell's to be thrown away, and new ListCell's created with the
     * new cell factory.
     */
    public final void setCellFactory(Callback<ListView<T>, ListCell<T>> value) {
        cellFactoryProperty().set(value);
    }

    /**
     * <p>
     * Setting a custom cell factory has the effect of deferring all cell
     * creation, allowing for total customization of the cell. Internally, the
     * ListView is responsible for reusing ListCells - all that is necessary is
     * for the custom cell factory to return from this function a ListCell which
     * might be usable for representing any item in the ListView.
     *
     * <p>
     * Refer to the Cell class documentation for more detail.
     */
    public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
        if (cellFactory == null) {
            cellFactory = new SimpleObjectProperty<Callback<ListView<T>, ListCell<T>>>(
                    this, "cellFactory");
        }
        return cellFactory;
    }
}
