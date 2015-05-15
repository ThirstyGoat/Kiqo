package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Skill;
import javafx.collections.FXCollections;
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
    private ObservableList<TreeItem<Item>> children = FXCollections.observableArrayList();
    private Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();

    @SafeVarargs
    public GoatTreeItem(Item item, ObservableList<? extends Item>... elements) {
        setValue(item);
        for (ObservableList<? extends Item> childrenItems : elements) {
            // List of all children elements
            addChildren(childrenItems);
        }
    }

    private void addChildren(ObservableList<? extends Item> childItems) {
        // Create parent item to hold these children
        TreeItem<Item> parent = new TreeItem<>();

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
                parent.getChildren().add(treeItem);

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

        // Add parent node to the "super-parent"
        getChildren().add(parent);
        getChildren().add(new TreeItem<>(new Skill("this is a skill", "")));
    }

    @Override
    public ObservableList<TreeItem<Item>> getChildren() {
        return children;
    }
}