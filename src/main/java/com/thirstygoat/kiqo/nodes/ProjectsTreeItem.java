package com.thirstygoat.kiqo.nodes;

import com.thirstygoat.kiqo.model.Story;
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
        treeItem.getChildren().add(releases);
        treeItem.getChildren().add(stories);
        treeItem.setExpanded(true);
        return treeItem;
    }
}