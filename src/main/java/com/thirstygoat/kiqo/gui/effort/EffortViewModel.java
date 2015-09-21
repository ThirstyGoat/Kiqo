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
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

/**
 * Created by leroy on 19/09/15.
 */
public class EffortViewModel extends ModelViewModel<Effort> implements Editable {
    private FunctionBasedValidator<Person> personValidator;
    private CompositeValidator allValidator;
    private ObjectProperty<Effort> effort;

    private ObjectProperty<LocalDate> endDateProperty = new SimpleObjectProperty<>(LocalDate.now());
    private ObjectProperty<LocalTime> endTimeProperty = new SimpleObjectProperty<>(LocalTime.now());

    private StringProperty endDateStringProperty;


    public EffortViewModel() {
        super();
        personValidator = new FunctionBasedValidator<>(personProperty(),
                        person -> person != null && organisationProperty().get().getPeople().contains(person),
//                        // TODO check that person is in team associated with this sprint, when assignment is working.
                        ValidationMessage.error("Person must exist"));

        allValidator = new CompositeValidator();
        allValidator.addValidators(personValidator);
        effort = new SimpleObjectProperty<>();

        endDateStringProperty = new SimpleStringProperty("");
        endDateStringProperty.bind(Bindings.createStringBinding(() ->
                endDateProperty().get() != null ? endDateProperty().get().format(Utilities.DATE_FORMATTER)
                        : "", endDateProperty()));
    }

    @Override
    public void load(Effort item, Organisation organisation) {
        organisationProperty().set(organisation);

        if (item != null) {
            modelWrapper.set(item);
        } else {
            modelWrapper.set(modelSupplier().get());
            modelWrapper.reset();
            modelWrapper.commit();
        }
        effort.setValue(item);
        reload();
    }

    public Supplier<List<Person>> eligablePeopleSupplier = () -> {
        if (organisationProperty().get() != null) {
            return organisationProperty().get().getPeople();
        } else {
            return new ArrayList<>();
        }
    };

    @Override
    protected Supplier<Effort> modelSupplier() {
        return Effort::new;
    }

    @Override
    protected void afterLoad() {
//        endDateTimeProperty().bind(Bindings.createObjectBinding(() -> {
//            LocalDate endDate = endDateProperty.get();
//            LocalTime endTime = endTimeProperty().get();
//
//            LocalDateTime dateTime = LocalDateTime.of(
//                    endDate.getYear(),
//                    endDate.getMonth(),
//                    endDate.getDayOfMonth(),
//                    endTime.getHour(),
//                    endTime.getMinute()
//            );
//
//            dateTime.plusHours(endTimeProperty.get().getHour());
//            dateTime.plusMinutes(endTimeProperty.get().getMinute());
//            return dateTime;
//        }, endDateProperty, endTimeProperty));
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

    public ObjectProperty<LocalDateTime> logTimePRoperty() {
        return modelWrapper.field("logTime", Effort::getLogTime, Effort::setLogTime, LocalDateTime.now());
    }

    public ObjectProperty<LocalDateTime> endDateTimeProperty() {
        return modelWrapper.field("endTime", Effort::getEndTime, Effort::setEndTime, null);
    }

    public ObjectProperty<Duration> durationProperty() {
        return modelWrapper.field("duration", Effort::getDuration, Effort::setDuration, null);
    }

    public StringProperty commentProperty() {
        return modelWrapper.field("comment", Effort::getComment, Effort::setComment, "");
    }

    /** Extra Fields **/

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDateProperty;
    }

    public ObjectProperty<LocalTime> endTimeProperty() {
        return endTimeProperty;
    }

    /** Validation **/

    public ValidationStatus personValidation() {
        return personValidator.getValidationStatus();
    }

    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }

    public StringProperty endDateStringProperty() {
        return endDateStringProperty;
    }
}
