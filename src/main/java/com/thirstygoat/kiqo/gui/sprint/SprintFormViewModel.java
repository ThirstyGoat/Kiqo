package com.thirstygoat.kiqo.gui.sprint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Release;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.model.Team;
import com.thirstygoat.kiqo.util.StringConverters;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel implements IFormViewModel<Sprint> {
    
    private final StringProperty backlogShortNameProperty;
    private final StringProperty teamShortNameProperty;
    private final StringProperty releaseShortNameProperty;
    private final ObservableList<Story> sourceStories;
    private final BooleanProperty releaseEditableProperty;
    
    private boolean cancelled;
    private Runnable exitStrategy;
    
    public SprintFormViewModel() {
        super();
        backlogShortNameProperty = new SimpleStringProperty("");
        teamShortNameProperty = new SimpleStringProperty("");
        releaseShortNameProperty = new SimpleStringProperty("");
        sourceStories = FXCollections.observableArrayList(Story.getWatchStrategy());
        releaseEditableProperty = new SimpleBooleanProperty(false);
        
        backlogProperty().addListener((observable, oldValue, newValue) -> {
            loadGoatLists(sprint);
        });
    }

    /**
     * Loads information from the model into the GoatListSelectionView.
     */
    private void loadGoatLists(Sprint sprint) {
        if (sprint != null) {
            List<Story> source = new ArrayList<>();
            final Backlog backlog = sprint.getBacklog();
            if (backlog != null) {
                final Project project = backlog.getProject();
                source.addAll(project.getUnallocatedStories());
                project.getBacklogs().forEach(b -> {
                    source.addAll(b.getStories());
                });
                // remove existing targetStories from sourceStories
                source.removeAll(sprint.getStories());
            }
            
            sourceStories.setAll(source);
            stories().setAll(sprint.getStories());
        } else { // no sprint
            sourceStories.clear();
            stories().clear();
        }
    }

    @Override
    public void load(Sprint sprint, Organisation organisation) {
        this.organisation = organisation;
        this.sprint = sprint;

        // do after binding backlog name
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
            
            releaseEditableProperty.set(false);
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
            
            releaseEditableProperty.set(true);
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
        this.exitStrategy = exitStrategy;
    }

    @Override
    public Command createCommand() {
        if (cancelled) {
            return null;
        } else {
            return super.createCommand(); // null if no changes
        }
    }
    
    protected void okAction() {
        cancelled = false;
        exitStrategy.run();
    }

    protected void cancelAction() {
        cancelled = true;
        exitStrategy.run();
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

    protected ObservableList<Story> sourceStories() { 
        return sourceStories;
    }

    public BooleanExpression releaseEditableProperty() {
        return releaseEditableProperty;
    }
}
