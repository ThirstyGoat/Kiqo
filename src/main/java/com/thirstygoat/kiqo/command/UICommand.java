package com.thirstygoat.kiqo.command;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.viewModel.DetailsPaneController;
import com.thirstygoat.kiqo.viewModel.MainController;

import javafx.scene.control.ListView;


/**
 * Created by Carina Blair on 15/04/2015.
 */
public class UICommand extends Command<Void> {

    private final Command command;
    private ListView listView;
    private Item item;
    private DetailsPaneController detailsPaneController;

    public UICommand(Command<?> command) {
        this.command = command;
    }

    @Override
    public Void execute() {
        command.execute();
        refreshView();
        return null;
    }

    public Command getCommand(){
        return command;
    }

    private void refreshView() {
        if (item != null && listView != null) {
            MainController.triggerListUpdate(item, listView);
        }
    }

    @Override
    public void undo() {
        command.undo();
        refreshView();
    }

    @Override
    public String getType() {
        return command.getType();
    }

    public <T extends Item> void setRefreshParameters(T item, ListView<T> listView, DetailsPaneController detailsPaneController) {
        this.listView = listView;
        this.item = item;
        this.detailsPaneController = detailsPaneController;
    }
}
