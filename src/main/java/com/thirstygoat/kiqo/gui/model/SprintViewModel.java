package com.thirstygoat.kiqo.gui.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.Utilities;

import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;

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
//    private final ObservableList<Story> stories;

    private final FunctionBasedValidator<String> goalValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;
    private final FunctionBasedValidator<Backlog> backlogValidator;
    private final FunctionBasedValidator<LocalDate> startDateValidator;
    private final FunctionBasedValidator<LocalDate> endDateValidator;
    private final FunctionBasedValidator<Team> teamValidator;
    private final FunctionBasedValidator<Release> releaseValidator;


    public SprintViewModel() {
        goalProperty = new SimpleStringProperty("");
        longNameProperty = new SimpleStringProperty("");
        descriptionProperty = new SimpleStringProperty("");
        backlogProperty = new SimpleObjectProperty<>();
        startDateProperty = new SimpleObjectProperty<>();
        endDateProperty = new SimpleObjectProperty<>();
        teamProperty = new SimpleObjectProperty<>();
        releaseProperty = new SimpleObjectProperty<>();
//        stories = FXCollections.observableArrayList(Story.getWatchStrategy());

        goalValidator = new FunctionBasedValidator<>(goalProperty,
                string -> {
                    if (string.length() == 0|| string.length() > 20) {
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

        backlogValidator = new FunctionBasedValidator<>(backlogProperty, 
                backlog -> {
                    return false; // TODO
                },
                ValidationMessage.error("Backlog is not valid.")); // TODO
        
        startDateValidator = new FunctionBasedValidator<>(startDateProperty,
                date -> {
                    if (startDateProperty.get() == null) {
                        return false;
                    }
                    if (endDateProperty.get() == null) {
                        return true;
                    }
                    return startDateProperty.get().isBefore(endDateProperty.get());
                },
                ValidationMessage.error("Start date must be before end date."));

        endDateValidator = new FunctionBasedValidator<>(endDateProperty, 
                date -> {
                    return false; // TODO
                },
                ValidationMessage.error("End date is not valid.")); // TODO
        
        teamValidator = new FunctionBasedValidator<>(teamProperty, 
                team -> {
                    return false; // TODO
                },
                ValidationMessage.error("Team is not valid.")); // TODO
        
        releaseValidator = new FunctionBasedValidator<>(releaseProperty, 
                release -> {
                    return false; // TODO
                },
                ValidationMessage.error("Release is not valid.")); // TODO
    }



    public void load(Sprint sprint, Organisation organisation) {
//        this.organisationProperty.set(organisation);
        if (sprint != null) {
            goalProperty.set(sprint.shortNameProperty().get());
            longNameProperty.set(sprint.longNameProperty().get());
            descriptionProperty.set(sprint.descriptionProperty().get());
            backlogProperty.set(sprint.backlogProperty().get());
            startDateProperty.set(sprint.startDateProperty().get());
            endDateProperty.set(sprint.endDateProperty().get());
            teamProperty.set(sprint.teamProperty().get());
            releaseProperty.set(sprint.releaseProperty().get());
//            stories.clear();
//            stories.addAll(sprint.getStories());
        } else {
            goalProperty.set("");
            longNameProperty.set("");
            descriptionProperty.set("");
            backlogProperty.set(null);
            startDateProperty.set(null);
            endDateProperty.set(null);
            teamProperty.set(null);
            releaseProperty.set(null);
//            stories.clear();
        }
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

//    public ObservableList<Story> getStories() {
//        return stories;
//    }

    public FunctionBasedValidator<String> getGoalValidator() {
        return goalValidator;
    }

    public FunctionBasedValidator<String> getLongNameValidator() {
        return longNameValidator;
    }

    public FunctionBasedValidator<String> getDescriptionValidator() {
        return descriptionValidator;
    }

    // this should be set indirectly via backlog, shouldn't need any validation
    public FunctionBasedValidator<Backlog> getBacklogValidator() {
        return backlogValidator;
    }

    public FunctionBasedValidator<LocalDate> getStartDateValidator() {
        return startDateValidator;
    }

    public FunctionBasedValidator<LocalDate> getEndDateValidator() {
        return endDateValidator;
    }

    public FunctionBasedValidator<Team> getTeamValidator() {
        return teamValidator;
    }

    public FunctionBasedValidator<Release> getReleaseValidator() {
        return releaseValidator;
    }

//    public FunctionBasedValidator<ObservableList<Story>> getStoriesValidator() {
//        return storiesValidator;
//    }
}
