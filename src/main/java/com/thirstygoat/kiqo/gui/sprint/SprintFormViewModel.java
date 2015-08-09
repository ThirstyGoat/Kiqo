package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.IFormViewModel;
import com.thirstygoat.kiqo.model.*;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel implements IFormViewModel<Sprint> {
    
    private final ObservableList<Story> sourceStories;
    private final BooleanProperty releaseEditableProperty;
    
    private Runnable exitStrategy;
    
    public SprintFormViewModel() {
        super();
        sourceStories = FXCollections.observableArrayList(Story.getWatchStrategy());
        releaseEditableProperty = new SimpleBooleanProperty(false);
        
        backlogProperty().addListener((observable, oldValue, newValue) -> {
            loadGoatLists(super.sprintProperty().get());
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
        super.load(sprint, organisation);

        if (backlogProperty().get() != null) {
            sourceStories.clear();
            sourceStories.addAll(backlogProperty().get().getStories().stream()
                    .filter(story -> story.getIsReady() && !stories().contains(story)).collect(Collectors.toList()));
            releaseEditableProperty.set(false);
        } else {
            stories().clear();
            releaseEditableProperty.set(true);
        }
    }

    public void setListeners() {
        backlogProperty().addListener(((observable, oldValue, newValue) -> {
            sourceStories.clear();
            stories().clear();
            setStoryListProperties();
        }));
    }

    private void setStoryListProperties() {
        if (backlogProperty().get() != null) {
            sourceStories.addAll(backlogProperty().get().getStories().stream().filter(story -> story.getIsReady()).collect(Collectors.toList()));
        }
    }

    @Override
    public void setExitStrategy(Runnable exitStrategy) {
        this.exitStrategy = exitStrategy;
    }

    protected void okAction() {
        exitStrategy.run();
    }

    protected void cancelAction() {
        sprintProperty().set(null); // Potential hack way to ensure no command is made when the user cancels the dialog
        exitStrategy.run();
    }

    protected Supplier<List<Backlog>> getBacklogsSupplier() {
        return () -> {
            List<Backlog> list = new ArrayList<>();
            if (organisationProperty() != null) {
                organisationProperty().get().getProjects().forEach((project) -> list.addAll(project.getBacklogs()));
            }
            return list;
        };
    }

    protected Supplier<List<Team>> getTeamsSupplier() {
        return () -> {
            if (organisationProperty().get() != null) {
                return organisationProperty().get().getTeams();
            } else {
                return new ArrayList<>();
            }
        };
    }

    protected Supplier<List<Release>> getReleasesSupplier() {
        return () -> {
            List<Release> list = new ArrayList<>();
            if (organisationProperty().get() != null) {
                organisationProperty().get().getProjects().forEach((project) -> list.addAll(project.getReleases()));
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
