package com.thirstygoat.kiqo.viewModel;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.formControllers.FormController;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


/**
 * Created by samschofield on 16/07/15.
 */
public class TaskFormViewModel extends FormController<Task> {
    private Task task;
    private Organisation organisation;
    private Command<?> command;
    private boolean valid = false;

    private StringProperty nameProperty = new SimpleStringProperty("");
    private StringProperty descriptionProperty = new SimpleStringProperty("");
    private StringProperty estimateProperty = new SimpleStringProperty("");

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
                // Always valid as description isn't required and has no constraints
                s -> {
                    return s.matches("^([+-]?\\d*\\.?\\d*)$");
                },
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
    public Command<?> getCommand() { return command; }

    public void setCommand() {
        if (task == null) {
            task = new Task(nameProperty.get().trim(), descriptionProperty.get().trim(), Float.parseFloat(estimateProperty.get()));
            command = new CreateTaskCommand(task, this.story);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!task.getShortName().equals(shortNameProperty().get())) {
                changes.add(new EditCommand<>(task, "shortName", shortNameProperty().get()));
            }
            if (!task.getDescription().equals(descriptionProperty.get())) {
                changes.add(new EditCommand<>(task, "description", descriptionProperty.get()));
            }
            if (!task.estimateProperty().equals(estimateProperty().get())) {
                changes.add(new EditCommand<>(task, "estimate", Float.parseFloat(estimateProperty.get())));
            }
            command = new CompoundCommand("Edit Task", changes);
        }
    }

    @Override
    public boolean isValid() { return valid; }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
