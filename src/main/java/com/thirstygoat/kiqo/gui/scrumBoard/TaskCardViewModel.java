package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateImpedimentCommand;
import com.thirstygoat.kiqo.command.delete.DeleteImpedimentCommand;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Created by james on 19/08/15.
 */
public class TaskCardViewModel extends ModelViewModel<Task> implements Editable {
    private ObjectProperty<Task> task;
    private ObjectProperty<Organisation> organisation;
    private Stage stage;
    private Runnable exitStrategy;

    private ObservableRuleBasedValidator personValidator;
    private ObservableRuleBasedValidator shortNameValidator;
    private ObservableRuleBasedValidator descriptionValidator;
    private ObservableRuleBasedValidator teamValidator;
    private FunctionBasedValidator estimateValidator;
    private CompositeValidator allValidator;
    private StringProperty textFieldString = new SimpleStringProperty("");

    public TaskCardViewModel() {
        task = new SimpleObjectProperty<>();
        organisation = new SimpleObjectProperty<>();
        createValidators();
    }

    @Override
    public Supplier<Task> modelSupplier() {
        return Task::new;
    }

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(Utilities.SHORT_NAME_MAX_LENGTH), ValidationMessage.error("Name must be less than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters"));
        descriptionValidator = new ObservableRuleBasedValidator(); // always true

        teamValidator = new ObservableRuleBasedValidator(); // TODO add team validation - people must be from the sprints assigned team

        estimateValidator = new FunctionBasedValidator<>(estimateProperty().asString(),
                s -> s.matches("^[+]?([.]\\d+|\\d+[.]?\\d*)$") && s.length() > 0,
                ValidationMessage.error("Estimate must be a positive number"));

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, teamValidator);
    }

    @Override
    public Command getCommand() {
        final Command command;
        final ArrayList<Command> changes = new ArrayList<>();
        super.addEditCommands.accept(changes);

        if (!assignees().equals(modelWrapper.get().getAssigneesObservable())) {
            changes.add(new EditCommand<>(modelWrapper.get(), "assignees", new ArrayList<>(assignees().get())));
        }

        if (changes.size() > 0) {
            command = new CompoundCommand("Edit Skill", changes);
        } else {
            command = null;
        }
        return command;
    }

    public void load(Task task, Organisation organisation) {
        this.task.set(task);
        this.organisationProperty().setValue(organisation);
        if (task != null) {
            modelWrapper.set(task);
        }
        modelWrapper.reload();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    public StringProperty textFieldString() {
        return textFieldString;
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    public StringProperty shortNameProperty() {
        return modelWrapper.field("shortName", Task::shortNameProperty);
    }

    public StringProperty descriptionProperty() {
        return modelWrapper.field("description", Task::descriptionProperty);
    }

    public FloatProperty estimateProperty() {
        return modelWrapper.field("estimate", Task::getEstimate, Task::setEstimate);
    }

    public BooleanProperty blockedProperty() {
        return modelWrapper.field("blocked", Task::isBlocked, Task::setBlocked);
    }

    public ObservableList<Impediment> impedimentsObservableList() {
        return modelWrapper.field("impediments", Task::getImpediments, Task::setImpediments);
    }

    public ListProperty<Effort> loggedEffort() {
        return modelWrapper.field("loggedEffort", Task::getObservableLoggedEffort, Task::setLoggedEffort);
    }

    public ListProperty<Person> assignees() {
        return modelWrapper.field("assignees", Task::getAssigneesObservable, Task::setAssignees);
    }

    /** Other fields **/

    public ObjectProperty<Task> getTask() {
        return task;
    }

    public ListProperty<Person> eligibleAssignees() {
        ListProperty<Person> eligableAssignees = new SimpleListProperty<>(FXCollections.observableArrayList());

        Function<Task, List<Person>> getEligibleAssignees = task -> {

            if (task.getStory().getInSprint()) {
               return task.getStory().getSprint().getTeam().getTeamMembers().stream()
                                .filter(person -> !task.getAssigneesObservable().contains(person))
                                .collect(Collectors.toList());
            } else {
                return new ArrayList<Person>();
            }
        };

        task.addListener((observable, oldValue, newValue) -> {
            eligableAssignees.setAll(getEligibleAssignees.apply(newValue));
        });
        eligableAssignees.setAll(task.get() != null ? getEligibleAssignees.apply(task.get()) : new ArrayList<Person>());

        return eligableAssignees;
    }

    public ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus estimateValidation() {
        return estimateValidator.getValidationStatus();
    }

    @Override
    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {
        modelWrapper.reload();
    }

    public void addImpediment() {
        if (!textFieldString.get().isEmpty()) {
            CreateImpedimentCommand createImpedimentCommand = new CreateImpedimentCommand(new Impediment(textFieldString.get(), false), task.get());
            UndoManager.getUndoManager().doCommand(createImpedimentCommand);
            textFieldString.set("");
        }
    }

    public void removeImpediment(Impediment impediment) {
        if (impediment != null) {
            DeleteImpedimentCommand deleteImpedimentCommand = new DeleteImpedimentCommand(impediment, task.get());
            UndoManager.getUndoManager().doCommand(deleteImpedimentCommand);
        }
    }


    public ValidationStatus teamValidation() {
        return teamValidator.getValidationStatus();
    }
}
