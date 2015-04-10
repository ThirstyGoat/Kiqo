package seng302.group4.undo;

import javafx.scene.control.ListView;
import seng302.group4.Skill;
import seng302.group4.viewModel.MainController;

import java.util.ArrayList;

/**
 * Overwrites a field value
 *
 * @author bjk60
 *
 */
public class CompoundCommand extends Command<Void> {
    private ArrayList<Command<?>> commands = new ArrayList<>();
    private String type = "Compound Command";
    private ListView listView;
    private Object object;

    public CompoundCommand(final ArrayList<Command<?>> changes) {
        this.commands = changes;
    }

    @Override
    public Void execute() {
        this.commands.forEach(seng302.group4.undo.Command::execute);

        // Refresh listview
        if (object != null && listView != null) {
            MainController.triggerListUpdate(object, listView);
        }
        return null;
    }

    @Override
    public String toString() {
        return this.commands.size() + " changes";
    }

    @Override
    public void undo() {
        this.commands.forEach(seng302.group4.undo.Command::undo);

        // Refresh listview
        if (object != null && listView != null) {
            MainController.triggerListUpdate(object, listView);
        }
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type  = type;
    }

    public void setRefreshParameters(Object object, ListView listView) {
        this.listView = listView;
        this.object = object;
    }
}
