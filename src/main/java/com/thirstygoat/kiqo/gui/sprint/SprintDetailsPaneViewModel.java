package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.EditCommand;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

import com.thirstygoat.kiqo.gui.Loadable;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneViewModel extends SprintViewModel implements Loadable<Sprint> {
    public static final String PLACEHOLDER = "No stories in sprint";
    public ObjectProperty<EditCommand> commandObjectProperty;

    private final ListChangeListener<Story> listChangeListener;

    public SprintDetailsPaneViewModel() {
        listChangeListener = change -> {
            change.next();
            stories().setAll(change.getList());
        };

        commandObjectProperty = new SimpleObjectProperty<>();
        longNameProperty().addListener((observable, oldValue, newValue) -> {
            commandObjectProperty.set(new EditCommand(sprintProperty().get(), "longName", newValue));
        });
    }
}


