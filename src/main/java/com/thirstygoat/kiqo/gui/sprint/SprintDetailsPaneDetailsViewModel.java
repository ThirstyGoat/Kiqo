package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.MainController;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneDetailsViewModel extends SprintViewModel implements Editable {

    private StringProperty startDateStringProperty;
    private StringProperty endDateStringProperty;

    private MainController mainController;

    public SprintDetailsPaneDetailsViewModel() {
        super();
        startDateStringProperty = new SimpleStringProperty("");
        startDateStringProperty.bind(Bindings.createStringBinding(() -> {
            return startDateProperty().get() != null
                    ? startDateProperty().get().format(Utilities.DATE_FORMATTER)
                    : "";
        }, startDateProperty()));

        endDateStringProperty = new SimpleStringProperty("");
        endDateStringProperty.bind(Bindings.createStringBinding(() -> {
            return endDateProperty().get() != null
                    ? endDateProperty().get().format(Utilities.DATE_FORMATTER)
                    : "";
        }, endDateProperty()));
    }

    public void createTask() {
        mainController.createTask(tasksWithoutStoryProperty().get());
    }

    public void editTask(Task task) {
        mainController.editTask(task, tasksWithoutStoryProperty().get());
    }

    public void commitEdit() {
        Command command = createCommand();
        if (command != null ) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    public void cancelEdit() {
        reload();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    public static final String PLACEHOLDER = "No stories in sprint";

    protected StringProperty startDateStringProperty() {
        return startDateStringProperty;
    }

    protected StringProperty endDateStringProperty() {
        return endDateStringProperty;
    }
}


