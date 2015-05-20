package com.thirstygoat.kiqo.nodes;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.TreeNodeHeading;

/**
 * Represents an item with children in a TreeView
 * @author Bradley Kirwan
 */
public class GoatTreeItem<E extends Item> extends TreeItem<Item> {
    protected final Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();
    protected final ListChangeListener<Item> changeListener;

    public GoatTreeItem(String name, ObservableList<E> items, SelectionModel<TreeItem<Item>> selectionModel) {
        super(new TreeNodeHeading(name));
        changeListener = createChangeListener(selectionModel);
        addChildCollection(items);
        items.addListener(changeListener);
    }

    /**
     * @param item
     * @return treeItem corresponding to this item
     */
    protected TreeItem<Item> createTreeItem(final Item item) {
        return new TreeItem<Item>(item);
    }

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
                    // permute list order
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