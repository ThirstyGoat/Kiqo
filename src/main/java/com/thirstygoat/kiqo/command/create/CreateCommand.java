package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableItems;

/**
 * A wrapper to add/remove Searchables to the search index as they are "created" and "uncreated" in the model.
 * @author amy
 */
public abstract class CreateCommand extends Command {
    private Searchable obj;

    public CreateCommand(Searchable obj) {
        super();
        this.obj = obj;
    }
    
    /**
     * Adds the searchable to the model and the search index.
     */
    @Override
    public final void execute() {
        addToModel();
        SearchableItems.getInstance().addSearchable(obj);
        if (obj.getClass().getSuperclass() == Item.class) {
            if (!obj.getClass().equals(Task.class) && !obj.getClass().equals(AcceptanceCriteria.class)) {
                MainController.focusedItemProperty.set((Item) obj);
            }
        }
    }
    
    /**
     * Removes the searchable from the search index and the model.
     */
    @Override
    public final void undo() {
        SearchableItems.getInstance().removeSearchable(obj);
        removeFromModel();
    }

    /**
     * Add the object to the model (used in {@link #execute()}).
     */
    protected abstract void addToModel();
    
    /**
     * Remove the object from the model (used in {@link #undo()}).
     */
    protected abstract void removeFromModel();

    public Searchable getObj() {
        return obj;
    }
}
