package com.thirstygoat.kiqo.gui.scrumBoard;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateImpedimentCommand;
import com.thirstygoat.kiqo.command.delete.DeleteImpedimentCommand;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.sprint.SprintViewModel;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.stream.Collectors;


/**
 * Created by james on 19/08/15.
 */
public class TaskCardViewModel implements ViewModel, Editable {
    private GoatModelWrapper<Task> modelWrapper = new GoatModelWrapper<>();
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

    private void createValidators() {
        shortNameValidator = new ObservableRuleBasedValidator();
        shortNameValidator.addRule(shortNameProperty().isNotNull(), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().greaterThan(0), ValidationMessage.error("Name must not be empty"));
        shortNameValidator.addRule(shortNameProperty().length().lessThan(20), ValidationMessage.error("Name must be less than 20 characters"));
        descriptionValidator = new ObservableRuleBasedValidator(); // always true

        teamValidator = new ObservableRuleBasedValidator(); // TODO add team validation - people must be from the sprints assigned team

        estimateValidator = new FunctionBasedValidator<>(estimateProperty().asString(),
                s -> s.matches("^[+]?([.]\\d+|\\d+[.]?\\d*)$") && s.length() > 0,
                ValidationMessage.error("Estimate must be a positive number"));

        allValidator = new CompositeValidator(shortNameValidator, descriptionValidator, teamValidator);
    }

    public Command createCommand() {
        final Command command;
        final ArrayList<Command> changes = new ArrayList<>();

        if (shortNameProperty().get() != null && !shortNameProperty().get().equals(task.get().getShortName())) {
            changes.add(new EditCommand<>(task.get(), "shortName", shortNameProperty().get()));
        }
        if (descriptionProperty().get() != null && !descriptionProperty().get().equals(task.get().getDescription())) {
            changes.add(new EditCommand<>(task.get(), "description", descriptionProperty().get()));
        }
        if (estimateProperty().get() != task.get().estimateProperty().get()) {
            changes.add(new EditCommand<>(task.get(), "estimate", estimateProperty().get()));
        }
        if (blockedProperty().get() != task.get().blockedProperty().get()) {
            changes.add(new EditCommand<>(task.get(), "blocked", blockedProperty().get()));
        }

        if (changes.size() > 0) {
            command = new CompoundCommand("Edit Skill", changes);
        } else {
            command = null;
        }
        return command;
    }

    public void load(Task task) {
        this.task.set(task);
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
        return modelWrapper.field("isBlocked", Task::isBlocked, Task::setBlocked);
    }

    public ObservableList<Impediment> impedimentsObservableList() {
        return modelWrapper.field("impediments", Task::getImpediments, Task::setImpediments);
    }

    public ListProperty<Effort> loggedEffort() {
        return modelWrapper.field("loggedEffort", Task::getLoggedEffort, Task::setLoggedEffort);
    }

    public ListProperty<Person> assignedPeolpe() {
        return modelWrapper.field("assignedPeople", Task::getAssignedPeople, Task::setAssignedPeople);
    }

    public ObjectProperty<Task> getTask() {
        return task;
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
        Command command = createCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    @Override
    public void cancelEdit() {

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

    public ListProperty<Person> eligableAssignedPeople() {
        ListProperty<Person> eligableAssignedPeople = new SimpleListProperty<>(FXCollections.observableArrayList());
        Sprint sprint = null;
        for (Release release : organisation.get().getReleases()) {
            for (Sprint aSprint : release.getSprints()) {
                for (Story story : aSprint.getStories()) {
                    if (task.get() != null) {
                        if (story == task.get().getStory()) {
                            sprint = aSprint;
                        }
                    }
                }
            }
        }
        if (sprint != null) eligableAssignedPeople.setAll(sprint.getTeam().getTeamMembers());
        return eligableAssignedPeople;
    }
}
