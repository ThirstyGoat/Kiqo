package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by samschofield on 31/07/15.
 */
public class SprintViewModel implements ViewModel {
    protected Organisation organisation;
    protected Sprint sprint;
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
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final ObservableRuleBasedValidator storiesValidator;
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;
    private final CompositeValidator allValidator;

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
        stories = FXCollections.observableArrayList(Item.getWatchStrategy());

        goalValidator = new FunctionBasedValidator<>(goalProperty,
                string -> {
                    if (string == null || string.length() == 0 || string.length() > 20) {
                        return false;
                    }
                    final Backlog backlog = backlogProperty.get();
                    if (backlog == null) {
                        return true;
                    } else {
                        return Utilities.shortnameIsUnique(string, null, backlog.getProject().getSprints());
                    }
                },
                ValidationMessage.error("Sprint goal must be unique and not empty"));

        longNameValidator = new FunctionBasedValidator<>(longNameProperty,
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Long name must not be empty."));

        descriptionValidator = new FunctionBasedValidator<>(descriptionProperty,
                string -> {
                    return true;
                },
                ValidationMessage.error("Description is not valid."));

        backlogValidator = new FunctionBasedValidator<>(backlogProperty, backlog -> {
            if (backlog == null) {
                return ValidationMessage.error("Backlog must exist and not be empty");
            } else {
                return null;
            }
        });

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
                            if (startDateProperty.get() == null || endDateProperty().get() == null) {
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
                            if (releaseProperty.get() == null || endDateProperty().get() == null) {
                                return true;
                            } else {
                                return endDateProperty.get().isBefore(releaseProperty.get().getDate())
                                        || endDateProperty().get().isEqual(releaseProperty().get().getDate());
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

        allValidator = new CompositeValidator(goalValidator, longNameValidator, 
                descriptionValidator, backlogValidator, startDateValidator, 
                endDateValidator, teamValidator, releaseValidator, storiesValidator);
    }

    public void load(Sprint sprint, Organisation organisation) {
        this.organisation = organisation;
        bindStringProperties();
        this.sprint = sprint;
        if (sprint != null) {
            goalProperty.set(sprint.getShortName());
            longNameProperty.set(sprint.getLongName());
            descriptionProperty.set(sprint.getDescription());
            backlogProperty.set(sprint.getBacklog());
            startDateProperty.set(sprint.getStartDate());
            endDateProperty.set(sprint.getEndDate());
            teamProperty.set(sprint.getTeam());
            releaseProperty.set(sprint.getRelease());
            stories.clear();
            stories.addAll(sprint.getStories());
        } else {
            goalProperty.set("");
            longNameProperty.set("");
            descriptionProperty.set("");
            backlogProperty.set(null);
            startDateProperty.set(null);
            endDateProperty.set(null);
            teamProperty.set(null);
            releaseProperty.set(null);
            stories.clear();
        }
    }

    public void bindStringProperties() {
        // Implemented in children
    }

    public Command createCommand() {
        final Command command;
        if (sprint == null) {
            // new sprint command
            final Sprint sprint = new Sprint(goalProperty().get(), longNameProperty().get(),
                    descriptionProperty().getValue(), backlogProperty().get(), releaseProperty().get(), teamProperty().get(), startDateProperty().get(), endDateProperty().get(), stories());
            command = new CreateSprintCommand(sprint);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!goalProperty().get().equals(sprint.getShortName())) {
                changes.add(new EditCommand<>(sprint, "goal", goalProperty().get()));
            }
            if (!longNameProperty().get().equals(sprint.getLongName())) {
                changes.add(new EditCommand<>(sprint, "longName", longNameProperty().get()));
            }
            if (!descriptionProperty().get().equals(sprint.getDescription())) {
                changes.add(new EditCommand<>(sprint, "description", descriptionProperty().get()));
            }
            if (!backlogProperty().get().equals(sprint.getBacklog())) {
                changes.add(new EditCommand<>(sprint, "backlog", backlogProperty().get()));
            }
            if (!startDateProperty().get().equals(sprint.getStartDate())) {
                changes.add(new EditCommand<>(sprint, "startDate", startDateProperty().get()));
            }
            if (!endDateProperty().get().equals(sprint.getEndDate())) {
                changes.add(new EditCommand<>(sprint, "endDate", endDateProperty().get()));
            }
            if (!teamProperty().get().equals(sprint.getTeam())) {
                changes.add(new EditCommand<>(sprint, "team", teamProperty().get()));
            }
            if (!releaseProperty().get().equals(sprint.getRelease())) {
                changes.add(new MoveItemCommand<>(sprint, sprint.getRelease().getSprints(),
                        releaseProperty().get().getSprints()));
                changes.add(new EditCommand<>(sprint, "release", releaseProperty().get()));
            }
            // Stories being added to the sprint
            final ArrayList<Story> addedStories = new ArrayList<>();
            addedStories.addAll(stories);
            addedStories.removeAll(sprint.getStories());
            for (Story story : addedStories) {
                changes.add(new MoveItemCommand<>(story, addedStories, sprint.getStories()));
            }
            // Stories being removed from the sprint
            final ArrayList<Story> removedStories = new ArrayList<>(sprint.getStories());
            removedStories.removeAll(stories);
            for (Story story : removedStories) {
                changes.add(new MoveItemCommand<>(story, sprint.getStories(), removedStories));
            }

            command = new CompoundCommand("Edit Sprint", changes);
        }
        return command;
    }

    public Sprint getSprint() {
        return sprint;
    }
    public StringProperty goalProperty() {
        return goalProperty;
    }

    public StringProperty longNameProperty() {
        return longNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return backlogProperty;
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return startDateProperty;
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return endDateProperty;
    }

    public ObjectProperty<Team> teamProperty() {
        return teamProperty;
    }

    public ObjectProperty<Release> releaseProperty() {
        return releaseProperty;
    }

    public ReadOnlyBooleanProperty validProperty() {
        return allValidator.getValidationStatus().validProperty();
    }

    public ObservableList<Story> stories() {
        return stories;
    }

    public ValidationStatus goalValidation() {
        return goalValidator.getValidationStatus();
    }

    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus backlogValidation() {
        return backlogValidator.getValidationStatus();
    }

    public ValidationStatus startDateValidation() {
        return startDateValidator.getValidationStatus();
    }

    public ValidationStatus endDateValidation() {
        return endDateValidator.getValidationStatus();
    }

    public ValidationStatus teamValidation() {
        return teamValidator.getValidationStatus();
    }

    public ValidationStatus releaseValidation() {
        return releaseValidator.getValidationStatus();
    }

    public ValidationStatus storiesValidation() {
        return storiesValidator.getValidationStatus();
    }
    
    public ValidationStatus allValidation() {
        return allValidator.getValidationStatus();
    }
}
