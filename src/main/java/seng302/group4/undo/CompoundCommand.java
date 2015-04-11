package seng302.group4.undo;

import java.util.ArrayList;

import javafx.scene.control.ListView;
import seng302.group4.viewModel.DetailsPaneController;
import seng302.group4.viewModel.MainController;

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
    private DetailsPaneController detailsPaneController;

    public CompoundCommand(final ArrayList<Command<?>> changes) {
        commands = changes;
    }

    @Override
    public Void execute() {
        commands.forEach(seng302.group4.undo.Command::execute);
        refreshView();
        return null;
    }

    private void refreshView() {
        if (object != null && listView != null) {
            MainController.triggerListUpdate(object, listView);
        }
        detailsPaneController.showDetailsPane(object);
    }

    @Override
    public String toString() {
        return commands.size() + " changes";
    }

    @Override
    public void undo() {
        commands.forEach(seng302.group4.undo.Command::undo);
        refreshView();
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type  = type;
    }

    public <T> void setRefreshParameters(T object, ListView<T> listView, DetailsPaneController detailsPaneController) {
        this.listView = listView;
        this.object = object;
        this.detailsPaneController = detailsPaneController;
    }
}