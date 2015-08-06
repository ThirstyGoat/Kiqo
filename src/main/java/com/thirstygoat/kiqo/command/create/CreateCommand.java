package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.command.Command;
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
}
