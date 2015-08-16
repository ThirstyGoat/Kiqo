package com.thirstygoat.kiqo.gui.sprint;

import java.time.LocalDate;
import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.UpdateListCommand;
import com.thirstygoat.kiqo.command.create.CreateSprintCommand;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Team;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.mapping.ModelWrapper;
import de.saxsys.mvvmfx.utils.validation.CompositeValidator;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;

/**
 * Created by samschofield on 31/07/15.
 */
public class SprintViewModel implements ViewModel {
    private final ObjectProperty<Organisation> organisationProperty;
    private final ObjectProperty<Sprint> sprintProperty;
    private final ObservableList<Story> stories;
    private final ObservableRuleBasedValidator goalValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final ObservableRuleBasedValidator storiesValidator;
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;
    private final CompositeValidator allValidator;
    private ModelWrapper<Sprint> sprintWrapper = new ModelWrapper<>();

    private ListChangeListener<Story> storyListener = c -> stories().setAll(sprintWrapper.get().getStories());

    public SprintViewModel() {
        organisationProperty = new SimpleObjectProperty<>();
        sprintProperty = new SimpleObjectProperty<>();
        stories = FXCollections.observableArrayList(Story.getWatchStrategy());

        goalValidator = new ObservableRuleBasedValidator();

        BooleanBinding rule1 = goalProperty().isNotNull();
        BooleanBinding rule2 = goalProperty().length().greaterThan(0);
        BooleanBinding rule3 = goalProperty().length().lessThan(20);
        BooleanBinding rule4 = Bindings.createBooleanBinding(
                () -> {
                    if (releaseProperty().get() == null) {
                        return true;
                    }
                    for (Sprint sprint : releaseProperty().get().getSprints()) {
                        if (sprint.getShortName().equals(goalProperty().get())) {
                            if (!sprint.equals(sprintProperty().get())) {
                                // Checks sprint with same short name isn't itself
                                return false;
                            }
                        }
                    }
                    return true;
                }, goalProperty(), releaseProperty());

        goalValidator.addRule(rule1, ValidationMessage.error("Sprint goal must be unique and not empty"));
        goalValidator.addRule(rule2, ValidationMessage.error("Sprint goal must be unique and not empty"));
        goalValidator.addRule(rule3, ValidationMessage.error("Sprint goal must be unique and not empty"));
        goalValidator.addRule(rule4, ValidationMessage.error("Sprint goal must be unique and not empty"));

        backlogValidator = new FunctionBasedValidator<>(backlogProperty(), backlog -> {
            if (backlog == null) {
                return ValidationMessage.error("Backlog must exist and not be empty");
            } else {
                return null;
            }
        });

        longNameValidator = new FunctionBasedValidator<>(longNameProperty(),
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
        startDateValidator.addRule(startDateProperty().isNotNull(),
                ValidationMessage.error("Start date must not be empty"));
        startDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            if (endDateProperty().get() == null || startDateProperty().get() == null) {
                                return true;
                            } else {
                                return startDateProperty().get().isBefore(endDateProperty().get());
                            }
                        },
                        startDateProperty(), endDateProperty()),
                ValidationMessage.error("Start date must precede end date"));

        endDateValidator = new ObservableRuleBasedValidator();
        endDateValidator.addRule(endDateProperty().isNotNull(),
                ValidationMessage.error("End date must not be empty"));
        endDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            if (startDateProperty().get() == null || endDateProperty().get() == null) {
                                return true;
                            } else {
                                return endDateProperty().get().isAfter(startDateProperty().get());
                            }
                        },
                        endDateProperty(), startDateProperty()),
                ValidationMessage.error("End date must be after start date"));
        endDateValidator.addRule(
                Bindings.createBooleanBinding(
                        () -> {
                            // endDate null check is necessary for runtime correctness but impotent in terms of validation
                            if (releaseProperty().get() == null || endDateProperty().get() == null) {
                                return true;
                            } else {
                                return endDateProperty().get().isBefore(releaseProperty().get().getDate())
                                        || endDateProperty().get().isEqual(releaseProperty().get().getDate());
                            }
                        },
                        endDateProperty(), releaseProperty()),
                ValidationMessage.error("End date must precede release date"));

        teamValidator = new FunctionBasedValidator<>(teamProperty(), team -> {
            if (team == null) {
                return ValidationMessage.error("Team must exist and not be empty");
            } else {
                return null;
            }
        });

        releaseValidator = new FunctionBasedValidator<>(releaseProperty(), release -> {
            if (release == null) {
                return ValidationMessage.error("Release must exist");
            } else {
                return null;
            }
        });

        allValidator = new CompositeValidator(goalValidator, longNameValidator, backlogValidator, startDateValidator,
                endDateValidator, teamValidator, releaseValidator, storiesValidator);
    }

    public void load(Sprint sprint, Organisation organisation) {
        organisationProperty().set(organisation);
        sprintProperty().set(sprint);

        if (sprint != null) {
            sprintWrapper.set(sprint);
            sprint.initBoundPropertySupport();
            sprint.addPropertyChangeListener((observable) -> reload());
            if (sprintWrapper.get() != null) {
                sprintWrapper.get().getStories().removeListener(storyListener);
            }
            sprintWrapper.get().getStories().addListener(storyListener);
            stories().setAll(sprintWrapper.get().getStories());
        } else {
            sprintWrapper.set(new Sprint());
            sprintWrapper.reset();
            sprintWrapper.commit();
            stories().clear();
        }
        sprintWrapper.reload();
    }

    public void reset() {
        sprintWrapper.reset();
        stories().clear();
    }

    public void reload() {
        sprintWrapper.reload();
        stories().clear();
        stories().addAll(sprintProperty().get().getStories());
    }

    /**
     * 
     * @return command for creating or editing the active item. Null if no changes have been made.
     */
    public Command createCommand() {
        final Command command;
        if  (!allValidation().isValid()) {
            // Properties are not valid
            return null;
        }
        if  (!sprintWrapper.isDifferent() && !stories().containsAll(sprintProperty().get().getStories())
                && !sprintProperty().get().getStories().containsAll(stories)) {
            // Nothing changed
            return null;
        }
        if (sprintProperty.get() == null) {
            // new sprintProperty.get() command
            final Sprint sprint = new Sprint(goalProperty().get(), longNameProperty().get(),
                    descriptionProperty().getValue(), backlogProperty().get(), releaseProperty().get(), teamProperty().get(), startDateProperty().get(), endDateProperty().get(), stories());
            command = new CreateSprintCommand(sprint);
        } else {
            // edit command
            final ArrayList<Command> changes = new ArrayList<>();
            if (!goalProperty().get().equals(sprintProperty.get().getShortName())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "goal", goalProperty().get()));
            }
            if (!longNameProperty().get().equals(sprintProperty.get().getLongName())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "longName", longNameProperty().get()));
            }
            if (!descriptionProperty().get().equals(sprintProperty.get().getDescription())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "description", descriptionProperty().get()));
            }
            if (!backlogProperty().get().equals(sprintProperty.get().getBacklog())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "backlog", backlogProperty().get()));
            }
            if (!startDateProperty().get().equals(sprintProperty.get().getStartDate())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "startDate", startDateProperty().get()));
            }
            if (!endDateProperty().get().equals(sprintProperty.get().getEndDate())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "endDate", endDateProperty().get()));
            }
            if (!teamProperty().get().equals(sprintProperty.get().getTeam())) {
                changes.add(new EditCommand<>(sprintProperty.get(), "team", teamProperty().get()));
            }
            if (!releaseProperty().get().equals(sprintProperty.get().getRelease())) {
                changes.add(new MoveItemCommand<>(sprintProperty.get(), sprintProperty.get().getRelease().getSprints(),
                        releaseProperty().get().getSprints()));
                changes.add(new EditCommand<>(sprintProperty.get(), "release", releaseProperty().get()));
            }
            if (!(stories.containsAll(sprintProperty.get().getStories())
                    && sprintProperty.get().getStories().containsAll(stories()))) {
                changes.add(new UpdateListCommand<>("Move Stories to/from Sprint", stories, sprintProperty.get().getStories()));
            }

            sprintProperty.get().getStories().stream().filter(s -> !stories.contains(s)).forEach(s1 -> changes.add(new EditCommand<>(s1, "inSprint", false)));

            stories.forEach(s -> {
                changes.add(new EditCommand<>(s, "inSprint", true));
            });



            if (!changes.isEmpty()) {
                command = new CompoundCommand("Edit Sprint", changes);
            } else {
                command = null;
            }
        }
        return command;
    }
    
    public ObjectProperty<Organisation> organisationProperty() {
        return organisationProperty;
    }
    
    public ObjectProperty<Sprint> sprintProperty() {
        return sprintProperty;
    }
    
    public StringProperty goalProperty() {
        return sprintWrapper.field("goal", Sprint::getGoal, Sprint::setGoal, "");
    }

    public StringProperty longNameProperty() {
        return sprintWrapper.field("longName", Sprint::getLongName, Sprint::setLongName, "");
    }

    public StringProperty descriptionProperty() {
        return sprintWrapper.field("description", Sprint::getDescription, Sprint::setDescription, "");
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return sprintWrapper.field("backlog", Sprint::getBacklog, Sprint::setBacklog, null);
    }

    public ObjectProperty<LocalDate> startDateProperty() {
        return sprintWrapper.field("startDate", Sprint::getStartDate, Sprint::setStartDate);
    }

    public ObjectProperty<LocalDate> endDateProperty() {
        return sprintWrapper.field("endDate", Sprint::getEndDate, Sprint::setEndDate);
    }

    public ObjectProperty<Team> teamProperty() {
        return sprintWrapper.field("team", Sprint::getTeam, Sprint::setTeam);
    }

    public ObjectProperty<Release> releaseProperty() {
        return sprintWrapper.field("release", Sprint::getRelease, Sprint::setRelease);
    }

    public ObservableList<Story> stories() {
        return stories;
    }
    
    public ValidationStatus goalValidation() {
        return goalValidator.getValidationStatus();
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

    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
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
