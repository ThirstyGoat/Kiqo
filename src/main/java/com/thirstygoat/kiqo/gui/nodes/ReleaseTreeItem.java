package com.thirstygoat.kiqo.gui.nodes;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TreeItem;

/**
 * Represents a collection of sprints for display in a TreeView under a release
 * @author Sam Schofield
 */
public class ReleaseTreeItem extends GoatTreeItem<Sprint> {
    public ReleaseTreeItem(SelectionModel<TreeItem<Item>> selectionModel) {
        super("Releases", selectionModel, Utilities.LEXICAL_COMPARATOR);
    }

    @Override
    protected TreeItem<Item> createTreeItem(final Item item) {
        final GoatTreeItem<Sprint> sprints = new GoatTreeItem<>(item, selectionModel, Utilities.LEXICAL_COMPARATOR);
        sprints.setItems(((Release) item).getSprints());
        return sprints;
    }
}