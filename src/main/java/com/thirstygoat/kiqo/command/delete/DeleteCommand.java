package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.search.Searchable;
import com.thirstygoat.kiqo.search.SearchableItems;

public abstract class DeleteCommand extends Command {
    private Searchable obj;

    public DeleteCommand(Searchable obj) {
        super();
        this.obj = obj;
    }

    @Override
    public final void execute() {
        SearchableItems.getInstance().removeSearchable(obj);
        removeFromModel();
    }

    @Override
    public final void undo() {
        addToModel();
        SearchableItems.getInstance().addSearchable(obj);
    }

    /**
     * Remove the object from the model (used in undo()).
     */
    protected abstract void removeFromModel();

    /**
     * Add the object to the model (used in execute()).
     */
    protected abstract void addToModel();
}