package com.thirstygoat.kiqo.gui.model;

import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.utils.validation.FunctionBasedValidator;
import de.saxsys.mvvmfx.utils.validation.ValidationMessage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableListValue;
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
    private final ObjectProperty<Project> projectProperty;
    private final ObjectProperty<LocalDate> startDateProperty;
    private final ObjectProperty<LocalDate> endDateProperty;
    private final ObjectProperty<Team> teamProperty;
    private final ObjectProperty<Release> releaseProperty;
    private final ObservableList<Story> storiesProperty;

    private final FunctionBasedValidator<String> goalValidator;
    private final FunctionBasedValidator<String> longNameValidator;
    private final FunctionBasedValidator<String> descriptionValidator;
//    private final FunctionBasedValidator<Project> projectValidator;
    private final FunctionBasedValidator<LocalDate> startDateValidator;
//    private final FunctionBasedValidator<LocalDate> endDateValidator;
//    private final FunctionBasedValidator<Team> teamValidator;
//    private final FunctionBasedValidator<Release> releaseValidator;
//    private final FunctionBasedValidator<ObservableList<Story>> storiesValidator;


    public SprintViewModel() {
        goalProperty = new SimpleStringProperty("");
        longNameProperty = new SimpleStringProperty("");
        descriptionProperty = new SimpleStringProperty("");
        projectProperty = new SimpleObjectProperty<>();
        startDateProperty = new SimpleObjectProperty<>();
        endDateProperty = new SimpleObjectProperty<>();
        teamProperty = new SimpleObjectProperty<>();
        releaseProperty = new SimpleObjectProperty<>();
        storiesProperty = FXCollections.observableArrayList(Story.getWatchStrategy());

        goalValidator = new FunctionBasedValidator<>(goalProperty,
                string -> {
                    if (string.length() == 0|| string.length() > 20) {
                        return false;
                    }
                    final Project project = projectProperty.get();
                    if (project == null) {
                        return true;
                    } else {
                        return Utilities.shortnameIsUnique(string, null, project.getSprints());
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

//        projectValidator = new FunctionBasedValidator<>(projectProperty, null);
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

//        endDateValidator = new FunctionBasedValidator<>(endDateProperty, null);
//        teamValidator = new FunctionBasedValidator<>(teamProperty, null);
//        releaseValidator = new FunctionBasedValidator<>(releaseProperty, null);
//        storiesValidator = new FunctionBasedValidator<>(storiesProperty, null);
    }



//    public void load(Backlog backlog, Organisation organisation) {
//        this.organisationProperty.set(organisation);
//        if (backlog != null) {
//            shortNameProperty.set(backlog.shortNameProperty().get());
//            longNameProperty.set(backlog.longNameProperty().get());
//            descriptionProperty.set(backlog.descriptionProperty().get());
//            productOwnerProperty.set(backlog.productOwnerProperty().get());
//            projectProperty.set(backlog.projectProperty().get());
//            scaleProperty.set(backlog.scaleProperty().get());
//            stories.clear();
//            stories.addAll(backlog.getStories());
//        } else {
//            shortNameProperty.set("");
//            longNameProperty.set("");
//            descriptionProperty.set("");
//            productOwnerProperty.set(null);
//            projectProperty.set(null);
//            scaleProperty.set(null);
//            stories.clear();
//        }
//    }

    public StringProperty goalProperty() {
        return goalProperty;
    }

    public StringProperty longNameProperty() {
        return longNameProperty;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    public ObjectProperty<Project> projectProperty() {
        return projectProperty;
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
        return storiesProperty;
    }

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
//    public FunctionBasedValidator<Project> getProjectValidator() {
//        return projectValidator;
//    }

    public FunctionBasedValidator<LocalDate> getStartDateValidator() {
        return startDateValidator;
    }

//    public FunctionBasedValidator<LocalDate> getEndDateValidator() {
//        return endDateValidator;
//    }
//
//    public FunctionBasedValidator<Team> getTeamValidator() {
//        return teamValidator;
//    }
//
//    public FunctionBasedValidator<Release> getReleaseValidator() {
//        return releaseValidator;
//    }

//    public FunctionBasedValidator<String> getStoriesValidator() {
////        return storiesValidator;
//    }
}
