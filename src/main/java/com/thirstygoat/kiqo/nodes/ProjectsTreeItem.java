package com.thirstygoat.kiqo.nodes;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.util.Utilities;

/**
 * Represents a collection of Projects for display in a TreeView
 * @author Bradley Kirwan
 */
public class ProjectsTreeItem extends GoatTreeItem<Project> {
    private final SelectionModel<TreeItem<Item>> selectionModel;

    public ProjectsTreeItem(ObservableList<Project> projects, SelectionModel<TreeItem<Item>> selectionModel) {
        super("Projects", projects, selectionModel);
        this.selectionModel = selectionModel;
    }

    @Override
    protected TreeItem<Item> createTreeItem(final Item item) {
        final TreeItem<Item> treeItem = new TreeItem<>(item);
        treeItem.getChildren().add(new GoatTreeItem<Release>("Releases", Utilities.createSortedList(((Project) item).observableReleases()), selectionModel));
        treeItem.setExpanded(true);
        return treeItem;
    }
}