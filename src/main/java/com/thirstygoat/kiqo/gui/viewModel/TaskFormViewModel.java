package com.thirstygoat.kiqo.gui.viewModel;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.create.CreateTaskCommand;
import com.thirstygoat.kiqo.gui.formControllers.FormController;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;


/**
 * Created by samschofield on 16/07/15.
 */
public class TaskFormViewModel extends FormController<Task> {
    private Task task;
    private Organisation organisation;
    private boolean valid = false;
    private Command command;

    private StringProperty nameProperty = new SimpleStringProperty("");
    private StringProperty descriptionProperty = new SimpleStringProperty("");
    private StringProperty estimateProperty = new SimpleStringProperty("");
    private ObjectProperty<Status> statusProperty = new SimpleObjectProperty<>();

    private FunctionBasedValidator nameValidator;
    private FunctionBasedValidator descriptionValidator;
    private FunctionBasedValidator estimateValidator;

    private CompositeValidator formValidator;
    private Story story;

    public TaskFormViewModel() {

    }

    private void initValidators() {
        nameValidator = new FunctionBasedValidator<>(nameProperty,
                // Check that length of the shortName isn't 0 or greater than 20 and that it is unique.
                s -> {
                    if (s.length() == 0 || s.length() > 20) {
                        return false;
                    }
                    Collection<Collection<? extends Item>> existingTasks = new ArrayList<>();
                    existingTasks.add(story.observableTasks());
                    return Utilities.shortnameIsUniqueMultiple(s, task, existingTasks);
                },
                ValidationMessage.error("Short name must be unique and not empty"));

        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty,
                // Always valid as description isn't required and has no constraints
                s -> true,
                ValidationMessage.error(""));

        estimateValidator = new FunctionBasedValidator<>(estimateProperty,
                s -> s.matches("^([+-]?\\d*\\.?\\d*)$") && s.length() > 0,
                ValidationMessage.error("Estimate must be a number"));

        formValidator = new CompositeValidator();
        formValidator.addValidators(nameValidator, descriptionValidator, estimateValidator);
    }

    /**
     * Sets all properties to be that of model. So for example if you change the task using,
     * setTask(), and you want to update the text fields with the new stories data, then you
     * should call this method.
     */
    public void reloadFromModel() {
        if (task != null) {
            nameProperty.set(task.getShortName());
            descriptionProperty.set(task.getDescription());
            estimateProperty.set(Float.toString(task.getEstimate()));
            statusProperty.set(task.getStatus());
        }
    }

    public StringProperty shortNameProperty() {
        return nameProperty;
    }



    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }
    

    public StringProperty estimateProperty() {
        return estimateProperty;
    }

    public ObjectProperty<Status> statusProperty() { return statusProperty; }


    public ValidationStatus nameValidation() {
        return nameValidator.getValidationStatus();
    }


    public ValidationStatus descriptionValidation() { return descriptionValidator.getValidationStatus(); }

    public ValidationStatus estimationValidation() { return estimateValidator.getValidationStatus(); }



    public ValidationStatus formValidation() {
        return formValidator.getValidationStatus();
    }

    public void setTask(Task task) {
        this.task = task;
        reloadFromModel();
    }

    public void setStory(Story story) {
        this.story = story;
        initValidators();
    }

    @Override
    public void setStage(Stage stage) {

    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @Override
    public void populateFields(Task task) {

    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command getCommand() { return command; }

    public void setCommand() {
        if (task == null) {
            float estimate = 0;
            if (!estimateProperty().get().trim().equals("")) {
                estimate = Float.parseFloat(estimateProperty.get());
            }
            task = new Task(nameProperty.get().trim(), descriptionProperty.get().trim(), estimate);
            command = new CreateTaskCommand(task, this.story);
            valid = true;
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!task.getShortName().equals(shortNameProperty().get())) {
                changes.add(new EditCommand<>(task, "shortName", shortNameProperty().get()));
            }
            if (!task.getDescription().equals(descriptionProperty.get())) {
                changes.add(new EditCommand<>(task, "description", descriptionProperty.get()));
            }
            if (task.estimateProperty().get() != Float.parseFloat(estimateProperty().get())) {
                changes.add(new EditCommand<>(task, "estimate", Float.parseFloat(estimateProperty.get())));
            }
            if (statusProperty().getValue() != task.getStatus()) {
                changes.add(new EditCommand<>(task, "status", statusProperty.getValue()));
            }
            command = new CompoundCommand("Edit Task", changes);
            valid = !changes.isEmpty();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
