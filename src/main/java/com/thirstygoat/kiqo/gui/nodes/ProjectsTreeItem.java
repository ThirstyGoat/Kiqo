package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

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

        final BacklogsTreeItem backlogs = new BacklogsTreeItem(selectionModel);
        backlogs.setItems(((Project) item).observableBacklogs());

        final GoatTreeItem<Story> stories = new GoatTreeItem<>("Stories", selectionModel, Utilities.LEXICAL_COMPARATOR);
        stories.setItems(((Project) item).observableUnallocatedStories());

        final GoatTreeItem<Release> releases = new GoatTreeItem<>("Releases", selectionModel, Utilities.LEXICAL_COMPARATOR);
        releases.setItems(((Project) item).observableReleases());

        treeItem.getChildren().add(backlogs);
        treeItem.getChildren().add(stories);
        treeItem.getChildren().add(releases);

        treeItem.setExpanded(true);
        return treeItem;
    }
}