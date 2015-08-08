package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableItems;

/**
 * A wrapper to remove/add Searchables to the search index as they are deleted and reinstated.
 * @author amy
 */
public abstract class DeleteCommand extends Command {
    private Searchable obj;

    public DeleteCommand(Searchable obj) {
        super();
        this.obj = obj;
    }

    /**
     * Removes the searchable from the search index and the model.
     */
    @Override
    public final void execute() {
        SearchableItems.getInstance().removeSearchable(obj);
        removeFromModel();
    }

    /**
     * Adds the searchable to the model and the search index.
     */
    @Override
    public final void undo() {
        addToModel();
        SearchableItems.getInstance().addSearchable(obj);
        if (obj.getClass().getSuperclass() == Item.class) {
            MainController.focusedItemProperty.set((Item) obj);
        }
    }

    /**
     * Removes the object from the model (used in {@link #execute()}).
     */
    protected abstract void removeFromModel();

    /**
     * Adds the object to the model (used in {@link #undo()}).
     */
    protected abstract void addToModel();
}