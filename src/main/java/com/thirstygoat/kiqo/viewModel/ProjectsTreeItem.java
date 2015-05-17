package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Project;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an item with children in a TreeView
 * @author Bradley Kirwan
 */
public class ProjectsTreeItem extends TreeItem<Item> {
    private Map<Project, TreeItem<Item>> treeItemMap = new HashMap<>();

    public ProjectsTreeItem(ObservableList<Project> element) {
        super();
        addChildren(element);
    }

    private void addChildren(ObservableList<Project> childItems) {
        // Add all children to the tree item
        for (Project item : childItems) {
            GoatTreeItem treeItem = new GoatTreeItem(item);
            treeItem.addChild("Releases", item.observableReleases());
            treeItem.addChild("Stories", item.observableStories());
            getChildren().add(treeItem);

            // Add release tree item to map
            treeItemMap.put(item, treeItem);
        }

        ListChangeListener<Project> listener = c -> {
            c.next();

            // Get added items and add them to this TreeItem
            for (Project item : c.getAddedSubList()) {
                GoatTreeItem treeItem = new GoatTreeItem(item);
                treeItem.addChild("Releases", item.observableReleases());
                treeItem.addChild("Stories", item.observableStories());
                int index = childItems.indexOf(item);
                getChildren().add(index, treeItem);

                // Add item tree item to map
                treeItemMap.put(item, treeItem);
            }

            for (Item item : c.getRemoved()) {
                getChildren().remove(treeItemMap.get(item));

                // Remove item from item tree map
                treeItemMap.remove(item);
            }
        };

        childItems.addListener(listener);
    }
}