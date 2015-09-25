package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.command.create.CreateSprintCommand;
import com.thirstygoat.kiqo.command.delete.DeleteTaskCommand;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.GoatModelWrapper;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by samschofield on 31/07/15.
 */
public class SprintViewModel implements ViewModel {
    private final ObjectProperty<Organisation> organisationProperty;
    private final ObjectProperty<Sprint> sprintProperty;
    private final ListProperty<Story> stories;
    private final ListProperty<Task> tasks;
    private final ListProperty<Story> eligibleStories;
    private final ObservableRuleBasedValidator goalValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final ObservableRuleBasedValidator descriptionValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final ObservableRuleBasedValidator storiesValidator;
    private final ObservableRuleBasedValidator startDateValidator;
    private final ObservableRuleBasedValidator endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;
    private final CompositeValidator allValidator;
    private final FloatProperty totalEstimatedHours;
    private final FloatProperty spentHours;
    protected GoatModelWrapper<Sprint> sprintWrapper = new GoatModelWrapper<>();
    private ObjectProperty<Story> tasksWithoutStory = new SimpleObjectProperty<>();
   
    public SprintViewModel() {
        organisationProperty = new SimpleObjectProperty<>();
        sprintProperty = new SimpleObjectProperty<>();
        stories = new SimpleListProperty<>(FXCollections.observableArrayList(Story.getWatchStrategy()));
        tasks = new SimpleListProperty<>(FXCollections.observableArrayList(Task.getWatchStrategy()));
        eligibleStories = new SimpleListProperty<>(FXCollections.observableArrayList());
        totalEstimatedHours = new SimpleFloatProperty(0);
        spentHours = new SimpleFloatProperty(0);
        
        // Set eligibleStories list, and listen for changes to backlog so that the list is updated
        eligibleStories.bind(Bindings.createObjectBinding(() -> {
        	if (backlogProperty().get() != null) {
                return backlogProperty().get().getStories();
            } else {
            	return FXCollections.observableArrayList();
            }
        }, backlogProperty()));
        
        goalValidator = new ObservableRuleBasedValidator();

        BooleanBinding uniqueShortName = Bindings.createBooleanBinding(
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

        goalValidator.addRule(goalProperty().isNotNull(), ValidationMessage.error("Sprint goal must not be empty"));
        goalValidator.addRule(goalProperty().length().greaterThan(0), ValidationMessage.error("Sprint goal must not be empty"));
        goalValidator.addRule(goalProperty().length().lessThan(Utilities.SHORT_NAME_MAX_LENGTH), ValidationMessage.error("Sprint goal must be less than " + Utilities.SHORT_NAME_MAX_LENGTH + " characters"));
        goalValidator.addRule(uniqueShortName, ValidationMessage.error("Sprint goal must be unique within backlog"));

        backlogValidator = new FunctionBasedValidator<>(backlogProperty(), backlog -> {
            if (backlog == null) {
                return ValidationMessage.error("Backlog must exist");
            } else {
                return null;
            }
        });

        longNameValidator = new FunctionBasedValidator<>(longNameProperty(),
                string -> {
                    if (string == null || string.length() == 0) {
                        return false;
                    } else {
                        return true;
                    }
                },
                ValidationMessage.error("Sprint name must not be empty"));

        descriptionValidator = new ObservableRuleBasedValidator(); // always true

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
                return ValidationMessage.error("Release must not be empty");
            } else {
                return null;
            }
        });

        allValidator = new CompositeValidator(goalValidator, longNameValidator, backlogValidator, startDateValidator,
                endDateValidator, teamValidator, releaseValidator, storiesValidator);
    }

    protected void deleteTasks(ObservableList<Task> tasks) {
        Command command;
        if (tasks.size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command> commands = new ArrayList<>();
            for (Task task : tasks) {

                commands.add(new DeleteTaskCommand(task, tasksWithoutStoryProperty().get()));
            }
            command = new CompoundCommand("Delete Task", commands);
        } else {
            final Task task = tasks.get(0);
            command = new DeleteTaskCommand(task, tasksWithoutStoryProperty().get());
        }

        UndoManager.getUndoManager().doCommand(command);
    }

    public void load(Sprint sprint, Organisation organisation) {
        organisationProperty().set(organisation);
        sprintProperty().set(sprint);

        if (sprint != null) {
            sprintWrapper.set(sprint);
            stories().clear();
            stories().setAll(sprintWrapper.get().getStories().filtered(s -> !s.getShortName().equals("Tasks without a Story")));
        } else {
            sprintWrapper.set(new Sprint());
            sprintWrapper.reset();
            sprintWrapper.commit();
            stories().clear();
        }
        sprintWrapper.reload();
        totalEstimatedHours.unbind();
        spentHours.unbind();

        if (sprintProperty.get() != null) {
            totalEstimatedHours.bind(sprintProperty().get().createTotalEstimateBinding());
            spentHours.bind(sprintProperty().get().createSpentEffortBinding());
        }


        // Listen for changes on model objects ObservableLists. This could be removed if ModelWrapper supported ListProperty
        sprintWrapper.get().getStories().addListener(new ListChangeListener<Story>() {
            @Override
            public void onChanged(Change<? extends Story> change) {
                stories().setAll(sprintWrapper.get().getStories());
            }
        });

        // Upon backlog change, target stories should be reset
        backlogProperty().addListener((observable, oldValue, newValue) -> stories().clear());

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
     * Supplies a list of eligable backlogs for this sprint
     */
    protected Supplier<List<Backlog>> backlogsSupplier() {
        return () -> {
            List<Backlog> list = new ArrayList<>();
            if (organisationProperty() != null) {
                organisationProperty().get().getProjects().forEach((project) -> list.addAll(project.getBacklogs()));
            }
            return list;
        };
    }

    /**
     * Supplies a list of eligable teams for this sprint
     */
    protected Supplier<List<Team>> teamsSupplier() {
        return () -> {
            if (organisationProperty().get() != null) {
                return organisationProperty().get().getTeams();
            } else {
                return new ArrayList<>();
            }
        };
    }

    /**
     * Supplies a list of eligable releases for this sprint
     */
    protected Supplier<List<Release>> releasesSupplier() {
        return () -> {
            List<Release> list = new ArrayList<>();
            if (organisationProperty().get() != null) {
                organisationProperty().get().getProjects().forEach((project) -> list.addAll(project.getReleases()));
            }
            return list;
        };
    }

    /**
     *
     * @return command for creating or editing the active item. Null if no changes have been made.
     */
    public Command createCommand() {
        final Command command;
        if (!allValidation().isValid()) {
            // Properties are not valid
            return null;
        }

        if (!sprintWrapper.isDifferent()
                && !stories().containsAll(sprintProperty().get().getStories())
                && !sprintProperty().get().getStories().containsAll(stories)) {
            // Nothing changed
            return null;
        }

        if (sprintProperty.get() == null) {
            // new sprintProperty.get() command
            List<Command> changes = new ArrayList<>();

            final Sprint sprint = new Sprint(goalProperty().get(), longNameProperty().get(),
                    descriptionProperty().getValue(), backlogProperty().get(), releaseProperty().get(), teamProperty().get(), startDateProperty().get(), endDateProperty().get(), stories());

            stories().forEach(s -> {
                if (!s.getInSprint()) {
                    changes.add(new EditCommand<>(s, "inSprint", true));
                    changes.add(new EditCommand<>(s, "sprint", sprintProperty.get()));
                }
            });

            changes.add(new CreateSprintCommand(sprint));
            command = new CompoundCommand("Create Sprint", changes);
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

            sprintProperty.get().getStories().stream().filter(s -> !stories.contains(s)).forEach(s1 -> {
                changes.add(new EditCommand<>(s1, "inSprint", false));
                changes.add(new EditCommand<>(s1, "sprint", null));
            });

            stories.forEach(s -> {
                if (!s.getInSprint()) {
                    changes.add(new EditCommand<>(s, "inSprint", true));
                    changes.add(new EditCommand<>(s, "sprint", sprintProperty.get()));
                }
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

    public ListProperty<Story> stories() {
        return stories;
    }

    public ListProperty<Task> tasks() {return tasks; }

    public ObjectProperty<Story> tasksWithoutStoryProperty() {
        return sprintWrapper.field("tasksWithoutStory", Sprint::getTasksWithoutStory, Sprint::setTasksWithoutStory); }

    public ListProperty<Story> eligibleStories() {
        return eligibleStories;
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

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
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

    public FloatProperty totalEstimatedHoursProperty() {
        return totalEstimatedHours;
    }

    public FloatProperty spentHoursProperty() {
        return spentHours;
    }

    public void changesBinding() {
        // TODO
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return sprintWrapper.dirtyProperty();
    }
}
