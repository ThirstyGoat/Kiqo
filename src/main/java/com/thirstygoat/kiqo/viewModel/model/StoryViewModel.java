package com.thirstygoat.kiqo.viewModel.model;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ObservableRuleBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import de.saxsys.mvvmfx.utils.validation.ValidationStatus;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by leroy on 28/07/15.
 */
public class StoryViewModel implements ViewModel {
    private final ObjectProperty<Story> story;
    private final ObjectProperty<Organisation> organisation;

    private final StringProperty shortName;
    private final StringProperty longName;
    private final StringProperty description;
    private final ObjectProperty<Person> creator;
    private final ObjectProperty<Project> project;
    private final ObjectProperty<Backlog> backlog;
    private final IntegerProperty priority;
    private final IntegerProperty estimate;
    private final ObjectProperty<Scale> scale;
    private final ObservableList<AcceptanceCriteria> acceptanceCriteria;
    private final ObservableList<Story> depdendencies;
    private final BooleanProperty isReady;
    private final ObservableList<Task> tasks;
    private final FloatProperty taskHours;

    private final FunctionBasedValidator shortNameValidator;
    private final FunctionBasedValidator longNameValidator;
    private final FunctionBasedValidator descriptionValidator;
    private final FunctionBasedValidator creatorValidator;
    private final FunctionBasedValidator projectValidator;
    private final ObservableRuleBasedValidator priorityValidator;
    private final FunctionBasedValidator scaleValidator;

    public StoryViewModel() {
        story = new SimpleObjectProperty<>();
        organisation = new SimpleObjectProperty<>();

        shortName = new SimpleStringProperty("");
        description = new SimpleStringProperty("");
        longName = new SimpleStringProperty("");
        estimate = new SimpleIntegerProperty();
        priority = new SimpleIntegerProperty();
        creator = new SimpleObjectProperty<>();
        project = new SimpleObjectProperty<>();
        backlog = new SimpleObjectProperty<>();
        scale = new SimpleObjectProperty<>();
        acceptanceCriteria = FXCollections.observableArrayList();
        depdendencies = FXCollections.observableArrayList();
        isReady = new SimpleBooleanProperty();
        tasks = FXCollections.observableArrayList();
        taskHours = new SimpleFloatProperty();

        shortNameValidator = new FunctionBasedValidator<>(shortName,
                // Check that length of the shortName isn't 0 or greater than 20 and that it is unique.
                s -> {
                    if (s.length() == 0 || s.length() > 20) {
                        return false;
                    }

                    if (project.get() == null) {
                        return true;
                    } else {
                        Collection<Collection<? extends Item>> existingStories = new ArrayList<>();
                        existingStories.add(project.get().getUnallocatedStories());
                        existingStories.addAll(project.get().getBacklogs().stream().map(Backlog::observableStories).collect(Collectors.toList()));

                        return Utilities.shortnameIsUniqueMultiple(s, story.get(), existingStories);
                    }
                },
                ValidationMessage.error("Short name must be unique and not empty"));

        longNameValidator = new FunctionBasedValidator<>(longName,
                // Checks that the long name isn't empty
                s -> s != null && !s.isEmpty(),
                ValidationMessage.error("Long name must not be empty"));

        descriptionValidator = new FunctionBasedValidator<>(description,
                // Always valid as description isn't required and has no constraints
                s -> true,
                ValidationMessage.error(""));

        creatorValidator = new FunctionBasedValidator<>(creator,
                // Checks that the creator exists within the organisation and is set
                s -> {
                    if (organisation.get() != null) {
                        for (final Person p : organisation.get().getPeople()) {
                            if (p == creator.get()) { return true;
                            }
                        }
                    }
                    return false;
                },
                ValidationMessage.error("Person must already exist"));

        projectValidator = new FunctionBasedValidator<>(project,
                p -> {
                    if (organisation.get() == null || p == null) {
                        return false;
                    } else {
                        return organisation.get().getProjects().contains(p);
                    }
                },
                ValidationMessage.error("Project must already exist"));

        priorityValidator = new ObservableRuleBasedValidator();
        priorityValidator.addRule(priority.greaterThan(Story.MIN_PRIORITY)
                .and(priority.lessThan(Story.MAX_PRIORITY)),
                ValidationMessage.error("Priority must be between "
                        + Story.MIN_PRIORITY + " and " + Story.MAX_PRIORITY));

        scaleValidator = new FunctionBasedValidator<>(scale,
                Utilities.emptinessPredicate(),
                ValidationMessage.error("Estimation Scale must not be empty"));
    }

    public void load(Story story, Organisation organisation) {
        this.organisation.set(organisation);

        if (story != null) {
            shortName.set(story.getShortName());
            longName.set(story.getLongName());
            description.set(story.getDescription());
            creator.set(story.getCreator());
            project.set(story.getProject());
            backlog.set(story.getBacklog());
            priority.set(story.getPriority());
            estimate.set(story.getEstimate());
            scale.set(story.getScale());
            acceptanceCriteria.clear();
            acceptanceCriteria.addAll(story.getAcceptanceCriteria());
            depdendencies.clear();
            depdendencies.addAll(story.getDependencies());
            isReady.set(story.getIsReady());
            tasks.clear();
            tasks.addAll(story.getTasks());
        } else {
            shortName.set("");
            longName.set("");
            description.set("");
            creator.set(null);
            project.set(null);
            backlog.set(null);
            priority.set(0);
            estimate.set(0);
            scale.set(null);
            acceptanceCriteria.clear();
            depdendencies.clear();
            isReady.set(false);
            tasks.clear();
        }
    }

    public Story getStory() {
        return story.get();
    }

    public ObjectProperty<Story> storyProperty() {
        return story;
    }

    public void setStory(Story story) {
        this.story.set(story);
    }

    public Organisation getOrganisation() {
        return organisation.get();
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation.set(organisation);
    }

    public String getShortName() {
        return shortName.get();
    }

    public StringProperty shortNameProperty() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    public String getLongName() {
        return longName.get();
    }

    public StringProperty longNameProperty() {
        return longName;
    }

    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public Person getCreator() {
        return creator.get();
    }

    public ObjectProperty<Person> creatorProperty() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator.set(creator);
    }

    public Project getProject() {
        return project.get();
    }

    public ObjectProperty<Project> projectProperty() {
        return project;
    }

    public void setProject(Project project) {
        this.project.set(project);
    }

    public Backlog getBacklog() {
        return backlog.get();
    }

    public ObjectProperty<Backlog> backlogProperty() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog.set(backlog);
    }

    public int getPriority() {
        return priority.get();
    }

    public IntegerProperty priorityProperty() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority.set(priority);
    }

    public int getEstimate() {
        return estimate.get();
    }

    public IntegerProperty estimateProperty() {
        return estimate;
    }

    public void setEstimate(int estimate) {
        this.estimate.set(estimate);
    }

    public Scale getScale() {
        return scale.get();
    }

    public ObjectProperty<Scale> scaleProperty() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale.set(scale);
    }

    public ObservableList<Story> getDepdendencies() {
        return depdendencies;
    }

    public boolean getIsReady() {
        return isReady.get();
    }

    public BooleanProperty isReadyProperty() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady.set(isReady);
    }

    public ObservableList<Task> getTasks() {
        return tasks;
    }

    public float getTaskHours() {
        return taskHours.get();
    }

    public FloatProperty taskHoursProperty() {
        return taskHours;
    }

    public void setTaskHours(float taskHours) {
        this.taskHours.set(taskHours);
    }

    public ValidationStatus shortNameValidation() {
        return shortNameValidator.getValidationStatus();
    }

    public ValidationStatus longNameValidation() {
        return longNameValidator.getValidationStatus();
    }

    public ValidationStatus descriptionValidation() {
        return descriptionValidator.getValidationStatus();
    }

    public ValidationStatus creatorValidation() {
        return creatorValidator.getValidationStatus();
    }

    public ValidationStatus projectValidation() {
        return projectValidator.getValidationStatus();
    }

    public ValidationStatus priorityValidation() {
        return priorityValidator.getValidationStatus();
    }

    public ValidationStatus scaleValidation() {
        return scaleValidator.getValidationStatus();
    }
}
