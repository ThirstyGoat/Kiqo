package com.thirstygoat.kiqo.viewModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.util.Utilities;

/**
 * Represents an item with children in a TreeView
 * @author Bradley Kirwan
 */
public class ProjectsTreeItem extends TreeItem<Item> {
    private static final Comparator<TreeItem<Item>> TREEITEM_COMPARATOR = (treeItem1, treeItem2) -> {
        return Utilities.LEXICAL_COMPARATOR.compare(treeItem1.getValue(), treeItem2.getValue());
    };
    private final Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();

    public ProjectsTreeItem(ObservableList<Project> element) {
        super();
        addChildren(element);
        ProjectsTreeItem.sortChildren(this);
    }

    private void addChildren(ObservableList<Project> childProjects) {
        // Add all children to the tree item
        for (final Project project : childProjects) {
            final GoatTreeItem projectTreeItem = new GoatTreeItem(project);
            projectTreeItem.addChild("Releases", project.observableReleases());
            getChildren().add(projectTreeItem);

            // Add release tree item to map
            treeItemMap.put(project, projectTreeItem);
        }

        final ListChangeListener<Project> listener = c -> {
            while (c.next()) {
                // get added projects and add them to this TreeItem
                for (final Project project : c.getAddedSubList()) {
                    final GoatTreeItem treeItem = new GoatTreeItem(project);
                    treeItem.addChild("Releases", project.observableReleases());
                    treeItem.setExpanded(true);

                    final int index = childProjects.indexOf(project);
                    getChildren().add(index, treeItem);
                    // Add item tree item to map
                    treeItemMap.put(project, treeItem);
                }

                for (final Project project : c.getRemoved()) {
                    getChildren().remove(treeItemMap.get(project));

                    // Remove item from item tree map
                    treeItemMap.remove(project);
                }
            }
            ProjectsTreeItem.sortChildren(this);
        };

        childProjects.addListener(listener);
    }

    private static void sortChildren(TreeItem<Item> parent) {
        parent.getChildren().sort(ProjectsTreeItem.TREEITEM_COMPARATOR);
    }
}