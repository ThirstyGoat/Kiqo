package com.thirstygoat.kiqo.gui.effort;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.command.create.CreateEffortCommand;
import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.gui.ModelViewModel;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Task;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Created by leroy on 19/09/15.
 */
public class EffortViewModel extends ModelViewModel<Effort> implements Editable {
    private CompositeValidator allValidator;
    private FunctionBasedValidator<Person> personValidator;
    private ObservableRuleBasedValidator endDateValidator;
    private FunctionBasedValidator<LocalTime> endTimeValidator;
    private FunctionBasedValidator<Duration> durationValidator;
    private FunctionBasedValidator<String> commentValidator;

    private ObjectProperty<Effort> effort;
    private ObjectProperty<LocalDate> endDateProperty = new SimpleObjectProperty<>();
    private ObjectProperty<LocalTime> endTimeProperty = new SimpleObjectProperty<>(LocalTime.now());
    private StringProperty endDateStringProperty;
    private ObjectProperty<Task> task = new SimpleObjectProperty<>();


    public EffortViewModel(Task task) {
        super();
        // TODO check that person is in team associated with this sprint, when assignment is working.
        this.task.set(task);
        effort = new SimpleObjectProperty<>();

        endDateStringProperty = new SimpleStringProperty("");
        endDateStringProperty.bind(
                Bindings.createStringBinding(() -> endDateProperty().get() != null ?
                        endDateProperty().get().format(Utilities.DATE_FORMATTER)
                        : "", endDateProperty())
        );

        ChangeListener listener = (observable, oldValue, newValue) -> {
            LocalDate endDate = endDateProperty.get();
            LocalTime endTime = endTimeProperty().get();
            LocalDateTime dateTime = LocalDateTime.of(
                    endDate.getYear(), endDate.getMonth(), endDate.getDayOfMonth(),
                    endTime.getHour(), endTime.getMinute()
            );
            dateTime.plusHours(endTimeProperty.get().getHour());
            dateTime.plusMinutes(endTimeProperty.get().getMinute());
            endDateTimeProperty().setValue(dateTime);
        };

        endDateProperty.addListener(listener);
        endTimeProperty.addListener(listener);
        endDateTimeProperty().setValue(LocalDateTime.now());

        initValidation();
    }

    private void initValidation() {
        allValidator = new CompositeValidator();

        personValidator = new FunctionBasedValidator<>(personProperty(),
                person -> person != null && organisationProperty().get().getPeople().contains(person),
                ValidationMessage.error("Person must exist"));

        endDateValidator = new ObservableRuleBasedValidator();
        endDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            if (endDateProperty().get() == null) {
                                return false;
                            } else if (endDateProperty().get().isBefore(task.get().getStory().getSprint().getStartDate()) ||
                                            endDateProperty().get().isAfter(task.get().getStory().getSprint().getEndDate())) {
                                return false;
                            }
                            return true;
                        },
                        endDateProperty),
                ValidationMessage.error("Logged date must fall within the sprint "));

        endTimeValidator = new FunctionBasedValidator<>(
                endTimeProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Must specify time")
        );


        commentValidator = new FunctionBasedValidator<>(
                commentProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Comment must be set")
        );

        durationValidator = new FunctionBasedValidator<>(
                durationProperty(),
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Duration must be set")
        );

        allValidator.addValidators(personValidator, endDateValidator, endTimeValidator, durationValidator, commentValidator);
    }

    @Override
    public void load(Effort item, Organisation organisation) {
        organisationProperty().set(organisation);
        effortObjectProperty().setValue(item);

        if (item != null) {
            modelWrapper.set(item);
        } else {
            modelWrapper.set(modelSupplier().get());
            modelWrapper.reset();
            modelWrapper.commit();
        }
        reload();
    }

    @Override
    protected Supplier<Effort> modelSupplier() {
        return Effort::new;
    }

    @Override
    public Command getCommand() {
        final Command command;

        if (effort.get() == null) {
            Effort effort = new Effort(personProperty().get(), taskProperty().get(), endDateTimeProperty().get(), durationProperty().get(), commentProperty().get());
            command = new CreateEffortCommand(effort, taskProperty().get());
        } else {
            if (!allValidation().isValid()) {
                LOGGER.log(Level.WARNING, "Fields are invalid, no command will be returned.");
                return null;
            } else if (!modelWrapper.isDirty()) {
                LOGGER.log(Level.WARNING, "Nothing changed. No command will be returned");
                return null;
            }

            final ArrayList<Command> changes = new ArrayList<>();
            super.addEditCommands.accept(changes);
            command = changes.size() == 1 ? changes.get(0) : new CompoundCommand("Edit Effort", changes);
        }
        return command;
    }

    public void commitEdit() {
        Command command = getCommand();
        if (command != null) {
            UndoManager.getUndoManager().doCommand(command);
        }
    }

    public void cancelEdit() {
        reload();
    }

    /** Model Fields **/

    public ObjectProperty<Person> personProperty() {
        return modelWrapper.field("person", Effort::getPerson, Effort::setPerson, null);
    }

    public ObjectProperty<Task> taskProperty() {
        return modelWrapper.field("task", Effort::getTask, Effort::setTask, null);
    }

    public ObjectProperty<LocalDateTime> endDateTimeProperty() {
        return modelWrapper.field("endDateTime", Effort::getEndDateTime, Effort::setEndDateTime, LocalDateTime.now());
    }

    public ObjectProperty<Duration> durationProperty() {
        return modelWrapper.field("duration", Effort::getDuration, Effort::setDuration, null);
    }

    public StringProperty commentProperty() {
        return modelWrapper.field("comment", Effort::getComment, Effort::setComment, "");
    }

    /** Extra Fields **/

    public ObjectProperty<LocalTime> endTimeProperty() {
        return endTimeProperty;
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDateProperty;
    }

    public ObjectProperty<Effort> effortObjectProperty() {
        return effort;
    }

    /** Validation **/

    public ValidationStatus personValidation() {
        return personValidator.getValidationStatus();
    }

    public ValidationStatus endTimeValidation() {
        return endTimeValidator.getValidationStatus();
    }

    public ValidationStatus endDateValidation() {
        return endDateValidator.getValidationStatus();
    }

    public ValidationStatus commentValidation() {
        return commentValidator.getValidationStatus();
    }

    public ValidationStatus durationValidation() {
        return durationValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }

    public ListProperty<Person> teamMembers() {
        ListProperty<Person> eligableAssignees = new SimpleListProperty<>(FXCollections.observableArrayList());
        Function<Task, List<Person>> getEligibleAssignees = task -> {
            if (task.getStory().getInSprint()) {
                return task.getStory().getSprint().getTeam().observableTeamMembers().stream()
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        };

        taskProperty().addListener((observable, oldValue, newValue) -> {
            eligableAssignees.clear();
            eligableAssignees.addAll(getEligibleAssignees.apply(newValue));
        });
        eligableAssignees.clear();
        eligableAssignees.addAll(taskProperty().get() != null ? getEligibleAssignees.apply(taskProperty().get()) : new ArrayList<>());

        return eligableAssignees;
    }

    public ListProperty<Person> eligibleAssignees() {
        ListProperty<Person> eligableAssignees = new SimpleListProperty<>(FXCollections.observableArrayList());
        Function<Task, List<Person>> getEligibleAssignees = task -> {
            if (task.getStory().getInSprint()) {
                return task.getStory().getSprint().getTeam().observableTeamMembers().stream()
                        .filter(person -> !task.getAssigneesObservable().contains(person))
                        .collect(Collectors.toList());
            } else {
                return new ArrayList<>();
            }
        };

        taskProperty().addListener((observable, oldValue, newValue) -> {
        	eligableAssignees.clear();
            eligableAssignees.addAll(getEligibleAssignees.apply(newValue));
        });
        eligableAssignees.clear();
        eligableAssignees.addAll(taskProperty().get() != null ? getEligibleAssignees.apply(taskProperty().get()) : new ArrayList<>());

        return eligableAssignees;
    }
}
