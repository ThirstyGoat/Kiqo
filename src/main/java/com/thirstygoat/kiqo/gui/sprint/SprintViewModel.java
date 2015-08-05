package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

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
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final ObservableRuleBasedValidator storiesValidator;
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;

    public SprintViewModel() {
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

        releaseValidator = new FunctionBasedValidator<>(releaseProperty,
                release -> release != null,
                ValidationMessage.error("Release must exist"));
    }
    
    public void load(Sprint sprint, Organisation organisation) {} 

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

    public ObservableList<Story> getStories() {
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
}
