package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by samschofield on 31/07/15.
 */
public class SprintViewModel implements ViewModel {
    private final StringProperty goalProperty; // This is the shortName
    private final StringProperty longNameProperty;
    private final StringProperty descriptionProperty;
    private final ObjectProperty<Backlog> backlogProperty;
    private final ObjectProperty<LocalDate> startDateProperty;
    private final ObjectProperty<LocalDate> endDateProperty;
    private final ObjectProperty<Team> teamProperty;
    private final ObjectProperty<Release> releaseProperty;
    private final ObservableList<Story> stories;
    private final FunctionBasedValidator<String> goalValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final ObservableRuleBasedValidator storiesValidator;
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;
    private final CompositeValidator allValidator;
    protected Organisation organisation;
    protected Sprint sprint;

    public SprintViewModel() {
        organisation = null;
        sprint = null;
        goalProperty = new SimpleStringProperty("");
        longNameProperty = new SimpleStringProperty("");
        descriptionProperty = new SimpleStringProperty("");
        backlogProperty = new SimpleObjectProperty<>();
        startDateProperty = new SimpleObjectProperty<>(null);
        endDateProperty = new SimpleObjectProperty<>(null);
        teamProperty = new SimpleObjectProperty<>();
        releaseProperty = new SimpleObjectProperty<>();
        stories = FXCollections.observableArrayList(Story.getWatchStrategy());

        goalValidator = new FunctionBasedValidator<>(goalProperty,
                string -> {
                    if (string == null || string.length() == 0 || string.length() > 20) {
                        return false;
                    }
                    // TODO unique within backlog, or project??
                    final Backlog backlog = backlogProperty.get();
                    if (backlog == null) {
                        return true;
                    } else {
                        return Utilities.shortnameIsUnique(string, null, backlog.getProject().getSprints());
                    }
                },
                ValidationMessage.error("Sprint goal must be unique and not empty"));

        backlogValidator = new FunctionBasedValidator<>(backlogProperty, backlog -> {
            if (backlog == null) {
                return ValidationMessage.error("Backlog must exist and not be empty");
            } else {
                return null;
            }
        });

        longNameValidator = new FunctionBasedValidator<>(longNameProperty,
                string -> {
                    if (string == null || string.length() == 0 || string.length() > 20) {
                        return false;
                    } else {
                        return true;
                    }
                },
                ValidationMessage.error("Sprint name must be unique and not empty"));

        storiesValidator = new ObservableRuleBasedValidator();
        storiesValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            for (Story story : stories) {
                                if (!story.getIsReady()) {
                                    return false;
                                }
                            }
                            return true;
                        },
                        stories),
                ValidationMessage.error("All stories must be marked as ready"));

        startDateValidator = new ObservableRuleBasedValidator();
        startDateValidator.addRule(startDateProperty.isNotNull(),
                ValidationMessage.error("Start date must not be empty"));
        startDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            if (endDateProperty.get() == null || startDateProperty.get() == null) {
                                return true;
                            } else {
                                return startDateProperty.get().isBefore(endDateProperty.get());
                            }
                        },
                        startDateProperty, endDateProperty),
                ValidationMessage.error("Start date must precede end date"));

        endDateValidator = new ObservableRuleBasedValidator();
        endDateValidator.addRule(endDateProperty.isNotNull(),
                ValidationMessage.error("End date must not be empty"));
        endDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            if (startDateProperty.get() == null || endDateProperty.get() == null) {
                                return true;
                            } else {
                                return endDateProperty.get().isAfter(startDateProperty.get());
                            }
                        },
                        endDateProperty, startDateProperty),
                ValidationMessage.error("End date must be after start date"));
        endDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            // endDate null check is necessary for runtime correctness but impotent in terms of validation
                            if (releaseProperty.get() == null || endDateProperty.get() == null) {
                                return true;
                            } else {
                                return endDateProperty.get().isBefore(releaseProperty.get().getDate())
                                        || endDateProperty.get().isEqual(releaseProperty.get().getDate());
                            }
                        },
                        endDateProperty, releaseProperty),
                ValidationMessage.error("End date must precede release date"));

        teamValidator = new FunctionBasedValidator<>(teamProperty, team -> {
            if (team == null) {
                return ValidationMessage.error("Team must exist and not be empty");
            } else {
                return null;
            }
        });

        releaseValidator = new FunctionBasedValidator<>(releaseProperty, release -> {
            if (release == null) {
                return ValidationMessage.error("Release must exist");
            } else {
                return null;
            }
        });

        allValidator = new CompositeValidator(goalValidator, longNameValidator, backlogValidator, startDateValidator,
                endDateValidator, teamValidator, releaseValidator, storiesValidator);
    }

    /**
     * 
     * @return command for creating or editing the active item. Null if no changes have been made.
     */
    public Command createCommand() {
        final Command command;
        if (sprint == null) {
            // new sprint command
            final Sprint sprint = new Sprint(goalProperty.get(), longNameProperty.get(),
                    descriptionProperty.getValue(), backlogProperty.get(), releaseProperty.get(), teamProperty.get(), startDateProperty.get(), endDateProperty.get(), stories());
            command = new CreateSprintCommand(sprint);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!goalProperty.get().equals(sprint.getShortName())) {
                changes.add(new EditCommand<>(sprint, "goal", goalProperty.get()));
            }
            if (!longNameProperty.get().equals(sprint.getLongName())) {
                changes.add(new EditCommand<>(sprint, "longName", longNameProperty.get()));
            }
            if (!descriptionProperty.get().equals(sprint.getDescription())) {
                changes.add(new EditCommand<>(sprint, "description", descriptionProperty.get()));
            }
            if (!backlogProperty.get().equals(sprint.getBacklog())) {
                changes.add(new EditCommand<>(sprint, "backlog", backlogProperty.get()));
            }
            if (!startDateProperty.get().equals(sprint.getStartDate())) {
                changes.add(new EditCommand<>(sprint, "startDate", startDateProperty.get()));
            }
            if (!endDateProperty.get().equals(sprint.getEndDate())) {
                changes.add(new EditCommand<>(sprint, "endDate", endDateProperty.get()));
            }
            if (!teamProperty.get().equals(sprint.getTeam())) {
                changes.add(new EditCommand<>(sprint, "team", teamProperty.get()));
            }
            if (!releaseProperty.get().equals(sprint.getRelease())) {
                changes.add(new MoveItemCommand<>(sprint, sprint.getRelease().getSprints(),
                        releaseProperty.get().getSprints()));
                changes.add(new EditCommand<>(sprint, "release", releaseProperty.get()));
            }
            if (!(stories.containsAll(sprint.getStories())
                    && sprint.getStories().containsAll(stories()))) {
                changes.add(new UpdateListCommand<Story>("Move Stories to/from Sprint", stories, sprint.getStories()));
            }
            if (!changes.isEmpty()) {
                command = new CompoundCommand("Edit Sprint", changes);
            } else {
                command = null;
            }
        }
        return command;
    }
    
    protected StringProperty goalProperty() {
        return goalProperty;
    }

    protected StringProperty longNameProperty() {
        return longNameProperty;
    }

    protected StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    protected ObjectProperty<Backlog> backlogProperty() {
        return backlogProperty;
    }

    protected ObjectProperty<LocalDate> startDateProperty() {
        return startDateProperty;
    }

    protected ObjectProperty<LocalDate> endDateProperty() {
        return endDateProperty;
    }

    protected ObjectProperty<Team> teamProperty() {
        return teamProperty;
    }

    protected ObjectProperty<Release> releaseProperty() {
        return releaseProperty;
    }

    protected ReadOnlyBooleanProperty validProperty() {
        return allValidator.getValidationStatus().validProperty();
    }

    protected ObservableList<Story> stories() {
        return stories;
    }

    protected ValidationStatus goalValidation() {
        return goalValidator.getValidationStatus();
    }

    protected ValidationStatus backlogValidation() {
        return backlogValidator.getValidationStatus();
    }

    protected ValidationStatus startDateValidation() {
        return startDateValidator.getValidationStatus();
    }

    protected ValidationStatus endDateValidation() {
        return endDateValidator.getValidationStatus();
    }

    protected ValidationStatus teamValidation() {
        return teamValidator.getValidationStatus();
    }

    protected ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }

    protected ValidationStatus releaseValidation() {
        return releaseValidator.getValidationStatus();
    }

    protected ValidationStatus storiesValidation() {
        return storiesValidator.getValidationStatus();
    }
    
    protected ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
