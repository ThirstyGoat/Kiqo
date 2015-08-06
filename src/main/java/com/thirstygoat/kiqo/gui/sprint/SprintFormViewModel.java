package com.thirstygoat.kiqo.gui.sprint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.StringConverters;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel implements IFormViewModel<Sprint> {
    private BooleanProperty canceled;
    private StringProperty backlogShortNameProperty;
    private StringProperty teamShortNameProperty;
    private StringProperty releaseShortNameProperty;
    private ObjectProperty<ObservableList<Story>> targetStoriesProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<Story>> sourceStoriesProperty = new SimpleObjectProperty<>();
    
    public SprintFormViewModel() {
        super();
        canceled = new SimpleBooleanProperty();
        backlogShortNameProperty = new SimpleStringProperty("");
        teamShortNameProperty = new SimpleStringProperty("");
        releaseShortNameProperty = new SimpleStringProperty("");
    }

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        this.organisation = organisation;
        this.sprint = sprint;
        
        bindStringProperties(organisation);
        
        if (sprint != null) {
            goalProperty().set(sprint.getShortName());
            longNameProperty().set(sprint.getLongName());
            descriptionProperty().set(sprint.getDescription());
            backlogProperty().set(sprint.getBacklog());
            startDateProperty().set(sprint.getStartDate());
            endDateProperty().set(sprint.getEndDate());
            teamProperty().set(sprint.getTeam());
            releaseProperty().set(sprint.getRelease());
            stories().clear();
            stories().addAll(sprint.getStories());
        } else {
            goalProperty().set("");
            longNameProperty().set("");
            descriptionProperty().set("");
            backlogProperty().set(null);
            startDateProperty().set(null);
            endDateProperty().set(null);
            teamProperty().set(null);
            releaseProperty().set(null);
            stories().clear();
        }
    }
    
    /**
     * The StringConverters must always be bound with the current organisation.
     * @param organisation
     */
    private void bindStringProperties(Organisation organisation) {
        backlogShortNameProperty.unbindBidirectional(backlogProperty());
        teamShortNameProperty.unbindBidirectional(teamProperty());
        releaseShortNameProperty.unbindBidirectional(releaseProperty());
        
        if (organisation != null) {
            backlogShortNameProperty.bindBidirectional(backlogProperty(),
                    StringConverters.backlogStringConverter(organisation));
            teamShortNameProperty.bindBidirectional(teamProperty(),
                    StringConverters.teamStringConverter(organisation));
            releaseShortNameProperty.bindBidirectional(releaseProperty(),
                    StringConverters.releaseStringConverter(organisation));
        }
    }

    @Override
    public void setExitStrategy(Runnable exitStrategy) {
        canceled.addListener((observable) -> {
            exitStrategy.run();
        });
    }

    public Boolean isCanceled() {
        return canceled.get();
    }

    protected void okAction() {
        canceled.set(true); // Set true first to trigger change on listener.
        canceled.set(false);
    }

    protected void cancelAction() {
        canceled.set(true);
    }

    protected StringProperty backlogShortNameProperty() {
        return backlogShortNameProperty;
    }

    protected StringProperty teamShortNameProperty() {
        return teamShortNameProperty;
    }

    protected StringProperty releaseShortNameProperty() {
        return releaseShortNameProperty;
    }

    protected Supplier<List<Backlog>> getBacklogsSupplier() {
        return () -> {
            List<Backlog> list = new ArrayList<>();
            if (organisation != null) {
                organisation.getProjects().forEach((project) -> list.addAll(project.getBacklogs()));
            }
            return list;
        };
    }

    protected Supplier<List<Team>> getTeamsSupplier() {
        return () -> {
            if (organisation != null) {
                return organisation.getTeams();
            } else {
                return new ArrayList<>();
            }
        };
    }

    protected Supplier<List<Release>> getReleasesSupplier() {
        return () -> {
            List<Release> list = new ArrayList<>();
            if (organisation != null) {
                organisation.getProjects().forEach((project) -> list.addAll(project.getReleases()));
            }
            return list;
        };
    }
}
