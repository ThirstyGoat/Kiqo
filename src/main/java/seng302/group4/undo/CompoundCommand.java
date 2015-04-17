package seng302.group4.undo;

import java.util.Collection;

import javafx.scene.control.ListView;
import seng302.group4.Item;
import seng302.group4.viewModel.DetailsPaneController;
import seng302.group4.viewModel.MainController;

/**
 * Wraps several Commands into an atomic unit. Similar to the idea of
 * transactions in database theory.
 *
 * @author bjk60
 *
 */
public class CompoundCommand extends Command<Void> {
    private final Collection<Command<?>> commands;
    private String type = "Compound Command";
    private ListView listView;
    private Item item;
    private DetailsPaneController detailsPaneController;

    /**
     * @param type short, user-friendly explanation of the functionality
     * @param commands collection of commands to be performed
     */
    public CompoundCommand(String type, final Collection<Command<?>> commands) {
        this.type = type;
        this.commands = commands;
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

    public <T extends Item> void setRefreshParameters(T item, ListView<T> listView, DetailsPaneController detailsPaneController) {
        this.listView = listView;
        this.item = item;
        this.detailsPaneController = detailsPaneController;
    }
}