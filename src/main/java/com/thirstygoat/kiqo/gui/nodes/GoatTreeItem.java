package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a textual heading and a collection of items for display in a TreeView
 *
 * @param <E> Element type for the collection of items
 * @author Bradley Kirwan
 */
public class GoatTreeItem<E extends Item> extends TreeItem<Item> {
    protected final Map<Item, TreeItem<Item>> treeItemMap;
    private final Comparator<TreeItem<Item>> treeItemComparator;
    protected SelectionModel<TreeItem<Item>> selectionModel;

    public GoatTreeItem(String name, SelectionModel<TreeItem<Item>> selectionModel, Comparator<Item> comparator) {
        super(new TreeNodeHeading(name));
        treeItemComparator = (treeItem1, treeItem2) -> comparator.compare(treeItem1.getValue(), treeItem2.getValue());
        treeItemMap = new HashMap<>();
        this.selectionModel = selectionModel;
    }

    public GoatTreeItem(Item item, SelectionModel<TreeItem<Item>> selectionModel, Comparator<Item> comparator) {
        super(item);
        treeItemComparator = (treeItem1, treeItem2) -> comparator.compare(treeItem1.getValue(), treeItem2.getValue());
        treeItemMap = new HashMap<>();
        this.selectionModel = selectionModel;
    }


    /**
     * Initialises a new TreeItem and adds children if required. Beware lack of type-checking (due to TreeView limitation); callers must ensure that item is an E.
     *
     * @param item value of new TreeItem (assumed to be an instance of type &lt;E&gt;)
     * @return a new TreeItem to represent this item in a TreeView
     */
    protected TreeItem<Item> createTreeItem(final Item item) {
        return new TreeItem<Item>(item);
    }

    /**
     * Adds a collection as children to the textual heading TreeItem
     *
     * @param items items added as children to the textual heading TreeItem
     */
    public void setItems(ObservableList<? extends Item> items) {
        // MUST add listener before adding children
        getChildren().clear();
        treeItemMap.clear();
        final ListChangeListener<Item> changeListener = createChangeListener(selectionModel, treeItemComparator);
        items.addListener(changeListener);
        for (final Item item : items) {
            final TreeItem<Item> treeItem = createTreeItem(item);
            getChildren().add(treeItem);
            treeItemMap.put(item, treeItem);
        }
        getChildren().sort(treeItemComparator);
    }

    private final ListChangeListener<Item> createChangeListener(SelectionModel<TreeItem<Item>> selectionModel, Comparator<TreeItem<Item>> treeItemComparator2) {
        return c -> {
            final TreeItem<Item> selectedItem = selectionModel.getSelectedItem();
            while (c.next()) {
                // add items
                for (final Item item : c.getAddedSubList()) {
                    final TreeItem<Item> treeItem = createTreeItem(item);
                    getChildren().add(c.getList().indexOf(item), treeItem);
                    treeItemMap.put(item, treeItem);
                }
                // remove items
                for (final Item item : c.getRemoved()) {
                    getChildren().remove(treeItemMap.get(item));
                    treeItemMap.remove(item);
                }
            }
            getChildren().sort(treeItemComparator);
            // prevents selecting a deleted item

            if (treeItemMap.values().contains(selectedItem)) {
                // select previously-selected item if not deleted
                selectionModel.select(selectedItem);
            } else if (selectionModel.selectedItemProperty().equals(this) && !getChildren().isEmpty()) {
                // if first child deleted but more remain, select first remaining child
                selectionModel.select(getChildren().get(0));
            }
        };
    }
}
