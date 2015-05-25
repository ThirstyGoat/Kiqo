package com.thirstygoat.kiqo.nodes;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

/**
 * Represents a collection of Projects for display in a TreeView
 * @author Bradley Kirwan
 */
public class BacklogsTreeItem extends GoatTreeItem<Backlog> {

    public BacklogsTreeItem(SelectionModel<TreeItem<Item>> selectionModel) {
        super("Backlogs", selectionModel, Utilities.LEXICAL_COMPARATOR);
    }

    @Override
    protected TreeItem<Item> createTreeItem(final Item item) {
        final GoatTreeItem<Item> treeItem = new GoatTreeItem<>(item, selectionModel, Utilities.LEXICAL_COMPARATOR);
        treeItem.setItems(((Backlog) item).observableStories());
        return treeItem;
    }
}