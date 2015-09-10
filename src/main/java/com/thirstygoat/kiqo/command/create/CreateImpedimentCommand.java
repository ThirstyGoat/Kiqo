package com.thirstygoat.kiqo.command.create;

import com.thirstygoat.kiqo.model.Impediment;
import com.thirstygoat.kiqo.model.Task;


/**
 * Created by james on 10/09/15.
 */
public class CreateImpedimentCommand extends CreateCommand{
    final private Impediment impediment;
    final private Task task;

    public CreateImpedimentCommand(final Impediment impediment, final Task task) {
        super(impediment);
        this.impediment = impediment;
        this.task = task;
    }

    @Override
    protected void addToModel() {
        task.getImpediments().add(impediment);
    }

    @Override
    protected void removeFromModel() {
        task.getImpediments().remove(impediment);
    }

    @Override
    public String getType() {
        return "<Create Impediment: \"" + impediment.getImpediment() + "\">";
    }

    @Override
    public String toString() {
        return "Create Impediment";
    }
}
