package seng302.group4.undo;

import java.util.ArrayList;

import javafx.scene.control.ListView;
import seng302.group4.Item;
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
    private Item item;
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
        if (item != null && listView != null) {
            MainController.triggerListUpdate(item, listView);
        }
        detailsPaneController.showDetailsPane(item);
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

    public <T extends Item> void setRefreshParameters(T item, ListView<T> listView, DetailsPaneController detailsPaneController) {
        this.listView = listView;
        this.item = item;
        this.detailsPaneController = detailsPaneController;
    }
}