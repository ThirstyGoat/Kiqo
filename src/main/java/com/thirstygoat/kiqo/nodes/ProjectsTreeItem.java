package com.thirstygoat.kiqo.viewModel;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Project;

/**
 * Represents an item with children in a TreeView
 * @author Bradley Kirwan
 */
public class ProjectsTreeItem extends TreeItem<Item> {
    private final Map<Item, TreeItem<Item>> treeItemMap = new HashMap<>();

    public ProjectsTreeItem(ObservableList<Project> projects, SelectionModel<TreeItem<Item>> selectionModel) {
        super();
        addChildren(projects, selectionModel);
    }

    private void addChildren(ObservableList<Project> childProjects, SelectionModel<TreeItem<Item>> selectionModel) {
        // define change listener
        final ListChangeListener<Project> listener = c -> {
            final ObservableList<? extends Project> newList = c.getList();
            final ObservableList<TreeItem<Item>> children = getChildren();
            final TreeItem<Item> selectedItem = selectionModel.getSelectedItem();
            while (c.next()) {
                if (c.wasPermutated()) {
                    for (int i = c.getFrom(); i < c.getTo(); ++i) {
                        children.set(i, treeItemMap.get(newList.get(i)));
                        final int permutation = c.getPermutation(i);
                        children.set(permutation, treeItemMap.get(newList.get(permutation)));
                    }
                } else if (c.wasUpdated()) {
                    // update item
                    final int i = c.getFrom();
                    children.set(i, treeItemMap.get(newList.get(i)));
                } else {
                    // get added projects and add them to this TreeItem
                    for (final Project project : c.getAddedSubList()) {
                        addProject(project, newList.indexOf(project));
                    }
                    for (final Project project : c.getRemoved()) {
                        children.remove(treeItemMap.get(project));

                        // Remove item from item tree map
                        treeItemMap.remove(project);
                    }
                }
            }
            selectionModel.select(selectedItem);
        };

        // add all children to the tree item
        for (final Project project : childProjects) {
            addProject(project, -1); // new item
        }
        childProjects.addListener(listener);
    }

    private void addProject(Project project, int index) {
        final GoatTreeItem projectTreeItem = new GoatTreeItem(project);
        projectTreeItem.addChild("Releases", project.observableReleases());
        if (index == -1) { // new item
            getChildren().add(projectTreeItem);
        } else {
            getChildren().add(index, projectTreeItem);
        }

        projectTreeItem.setExpanded(true);

        // Add release tree item to map
        treeItemMap.put(project, projectTreeItem);
    }
}