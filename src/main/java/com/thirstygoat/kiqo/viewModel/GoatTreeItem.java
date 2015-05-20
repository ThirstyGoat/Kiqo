package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.TreeNodeHeading;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item with children in a TreeView
 * @author Bradley Kirwan
 */
public class GoatTreeItem extends TreeItem<Item> {
    private Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();

    public GoatTreeItem(Item item) {
        super();
        setValue(item);
    }

    public void addChild(String name, ObservableList<? extends Item> childItems) {
        TreeItem<Item> parent = new TreeItem<>(new TreeNodeHeading(name));

        // Add all children to the tree item
        for (Item item : childItems) {
            TreeItem<Item> treeItem = new TreeItem<>(item);
            parent.getChildren().add(treeItem);

            // Add release tree item to map
            treeItemMap.put(item, treeItem);
        }

        ListChangeListener<Item> listener = c -> {
            c.next();

            // Get added items and add them to this TreeItem
            for (Item item : c.getAddedSubList()) {
                TreeItem<Item> treeItem = new TreeItem<>(item);
                int index = childItems.indexOf(item);
                parent.getChildren().add(index, treeItem);

                // Add item tree item to map
                treeItemMap.put(item, treeItem);
            }

            for (Item item : c.getRemoved()) {
                parent.getChildren().remove(treeItemMap.get(item));

                // Remove item from item tree map
                treeItemMap.remove(item);
            }
        };

        childItems.addListener(listener);

        getChildren().add(parent);
    }
}