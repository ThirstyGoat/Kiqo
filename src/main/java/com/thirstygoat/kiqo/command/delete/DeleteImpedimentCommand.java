package com.thirstygoat.kiqo.command.delete;

import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Task;


/**
 * Created by james on 10/09/15.
 */
public class DeleteImpedimentCommand extends DeleteCommand {
    final private Impediment impediment;
    final private Task task;


    public DeleteImpedimentCommand(final Impediment impediment, final Task task) {
        super(impediment);
        this.impediment = impediment;
        this.task = task;
    }

    @Override
    protected void removeFromModel() {
        task.getImpediments().remove(impediment);
    }

    @Override
    protected void addToModel() {
        task.getImpediments().add(impediment);
    }

    @Override
    public String getType() {
        return "<Delete Impediment: \"" + impediment.getImpediment() + "\">";
    }

    @Override
    public String toString() {
        return "Delete Impediment";
    }
}
