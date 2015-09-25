package com.thirstygoat.kiqo.gui.nodes;

import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.util.Utilities;


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
        treeItem.setItems(((Backlog) item).getStories());
        return treeItem;
    }
}