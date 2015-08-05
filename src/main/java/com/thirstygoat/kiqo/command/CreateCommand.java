package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableItems;

public abstract class CreateCommand extends Command {
    private Searchable obj;

    public CreateCommand(Searchable obj) {
        super();
        this.obj = obj;
    }

    @Override
    public final void execute() {
        addToModel();
        SearchableItems.getInstance().addSearchable(obj);
    }

    @Override
    public final void undo() {
        SearchableItems.getInstance().removeSearchable(obj);
        removeFromModel();
    }

    /**
     * Add the object to the model (used in execute()).
     */
    protected abstract void addToModel();
    
    /**
     * Remove the object from the model (used in undo()).
     */
    protected abstract void removeFromModel();
}
