package com.thirstygoat.kiqo.nodes;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Item;

/**
 * Represents a textual heading and a collection of items for display in a TreeView
 * @author Bradley Kirwan
 * @param <E> Element type for the collection of items
 */
public class GoatTreeItem<E extends Item> extends TreeItem<Item> {
    protected final Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();

    public GoatTreeItem(String name, ObservableList<E> items, SelectionModel<TreeItem<Item>> selectionModel) {
        //structure
        super(new TreeNodeHeading(name));
        addChildCollection(items);

        // behaviour
        items.addListener(createChangeListener(selectionModel));
    }

    /**
     * Initialises a new TreeItem and adds children if required. Beware lack of type-checking (due to TreeView limitation); callers must ensure that item is an E.
     * @param item value of new TreeItem (assumed to be an instance of type <E>)
     * @return a new TreeItem to represent this item in a TreeView
     */
    protected TreeItem<Item> createTreeItem(final Item item) {
        return new TreeItem<Item>(item);
    }

    /**
     * Adds a collection as children to the textual heading TreeItem
     * @param items items added as children to the textual heading TreeItem
     */
    private void addChildCollection(ObservableList<? extends Item> items) {
        for (final Item item : items) {
            final TreeItem<Item> treeItem = createTreeItem(item);
            getChildren().add(treeItem);
            treeItemMap.put(item, treeItem);
        }
    }

    private final ListChangeListener<Item> createChangeListener(SelectionModel<TreeItem<Item>> selectionModel) {
        return c -> {
            final ObservableList<? extends Item> newList = c.getList();
            final ObservableList<TreeItem<Item>> children = getChildren();
            final TreeItem<Item> selectedItem = selectionModel.getSelectedItem();
            while (c.next()) {
                if (c.wasPermutated()) {
                    // permute list order to reflect backing list
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        children.set(i, treeItemMap.get(newList.get(i)));
                        final int permutation = c.getPermutation(i);
                        children.set(permutation, treeItemMap.get(newList.get(permutation)));
                    }
                } else if (c.wasUpdated()) {
                    // update items
                    final int i = c.getFrom();
                    children.set(i, treeItemMap.get(newList.get(i)));
                } else {
                    // add items
                    for (final Item item : c.getAddedSubList()) {
                        final TreeItem<Item> treeItem = createTreeItem(item);
                        children.add(newList.indexOf(item), treeItem);
                        treeItemMap.put(item, treeItem);
                    }
                    // remove items
                    for (final Item item : c.getRemoved()) {
                        children.remove(treeItemMap.get(item));
                        treeItemMap.remove(item);
                    }
                }
            }
            selectionModel.select(selectedItem);
        };
    }
}