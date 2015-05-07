package com.thirstygoat.kiqo.command;

import javafx.scene.control.ListView;

import com.thirstygoat.kiqo.model.Item;
import com.thirstygoat.kiqo.viewModel.MainController;


/**
 * Created by Carina Blair on 15/04/2015.
 * @param <T> Type of item and listView to be updated
 */
public class UICommand<T extends Item> extends Command<Void> {

    private final Command<T> command;
    private ListView<T> listView;
    private T item;

    public UICommand(Command<T> command) {
        this.command = command;
    }

    @Override
    public Void execute() {
        command.execute();
        refreshView();
        return null;
    }

    public Command<T> getCommand(){
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

    public void setRefreshParameters(T item, ListView<T> listView) {
        this.listView = listView;
        this.item = item;
    }
}
