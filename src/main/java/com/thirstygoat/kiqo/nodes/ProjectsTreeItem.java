package com.thirstygoat.kiqo.nodes;

import com.thirstygoat.kiqo.model.*;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.util.Utilities;

/**
 * Represents a collection of Projects for display in a TreeView
 * @author Bradley Kirwan
 */
public class ProjectsTreeItem extends GoatTreeItem<Project> {
    public ProjectsTreeItem(SelectionModel<TreeItem<Item>> selectionModel) {
        super("Projects", selectionModel, Utilities.LEXICAL_COMPARATOR);
    }

    @Override
    protected TreeItem<Item> createTreeItem(final Item item) {
        final TreeItem<Item> treeItem = new TreeItem<>(item);
        final GoatTreeItem<Release> releases = new GoatTreeItem<>("Releases", selectionModel, Utilities.LEXICAL_COMPARATOR);
        releases.setItems(((Project) item).observableReleases());

        final GoatTreeItem<Story> stories = new GoatTreeItem<>("Stories", selectionModel, Utilities.LEXICAL_COMPARATOR);
        stories.setItems(((Project) item).observableStories());

        final BacklogsTreeItem backlogs = new BacklogsTreeItem(selectionModel);
        backlogs.setItems(((Project) item).observableBacklogs());

        treeItem.getChildren().add(releases);
        treeItem.getChildren().add(stories);
        treeItem.getChildren().add(backlogs);

        treeItem.setExpanded(true);
        return treeItem;
    }
}