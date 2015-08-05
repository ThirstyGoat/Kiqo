package com.thirstygoat.kiqo.gui.sprint;

import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateBacklogCommand;
import com.thirstygoat.kiqo.command.CreateSprintCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.command.MoveItemCommand;
import com.thirstygoat.kiqo.command.RemoveStoryFromBacklogCommand;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.StringConverters;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel implements IFormViewModel<Sprint> {
    private Sprint sprint;
    private StringProperty teamNameProperty;
    private StringProperty releaseNameProperty;
    private StringProperty backlogNameProperty;
    private ObjectProperty<ObservableList<Story>> targetStoriesProperty = new SimpleObjectProperty<>();
    private ObjectProperty<ObservableList<Story>> sourceStoriesProperty = new SimpleObjectProperty<>();
    
    public SprintFormViewModel() {
        super();
        teamNameProperty = new SimpleStringProperty("");
        releaseNameProperty = new SimpleStringProperty("");
        backlogNameProperty = new SimpleStringProperty("");
    }
    
    @Override
    public void load(Sprint sprint, Organisation organisation) {
        this.sprint = sprint;
        
        // replace bindings just in case organisation has been replaced
        teamNameProperty.unbind();
        releaseNameProperty.unbind();
        backlogNameProperty.unbind();
        teamNameProperty.bindBidirectional(super.teamProperty(), StringConverters.teamStringConverter(organisation));
        releaseNameProperty.bindBidirectional(super.releaseProperty(), StringConverters.releaseStringConverter(organisation));
        backlogNameProperty.bindBidirectional(super.backlogProperty(), StringConverters.backlogStringConverter(organisation));
        
        if (sprint != null) {
            super.goalProperty().set(sprint.shortNameProperty().get());
            super.longNameProperty().set(sprint.longNameProperty().get());
            super.descriptionProperty().set(sprint.descriptionProperty().get());
            super.backlogProperty().set(sprint.backlogProperty().get());
            super.startDateProperty().set(sprint.startDateProperty().get());
            super.endDateProperty().set(sprint.endDateProperty().get());
            super.teamProperty().set(sprint.teamProperty().get());
            super.releaseProperty().set(sprint.releaseProperty().get());
            super.stories().clear();
            super.stories().addAll(sprint.getStories());
        } else {
            super.goalProperty().set(null);
            super.longNameProperty().set(null);
            super.descriptionProperty().set(null);
            super.backlogProperty().set(null);
            super.startDateProperty().set(null);
            super.endDateProperty().set(null);
            super.teamProperty().set(null);
            super.releaseProperty().set(null);
            super.stories().clear();
        }
    }
    
    @Override
    public Command createCommand() {
        final Command command;
        if (sprint == null) {
            final Sprint sprint = new Sprint(goalProperty().get(), longNameProperty().get(),
                    descriptionProperty().getValue(), backlogProperty().get(), releaseProperty().get(), teamProperty().get(), startDateProperty().get(), endDateProperty().get(), stories());
            command = new CreateSprintCommand(sprint);
        } else {
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
            final ArrayList<Story> addedStories = new ArrayList<>(targetStoriesProperty.get());
            addedStories.removeAll(sprint.getStories());
            for (Story story : addedStories) {
                changes.add(new MoveItemCommand<>(story, addedStories, sprint.getStories()));
            }
            // Stories being removed from the sprint
            final ArrayList<Story> removedStories = new ArrayList<>(sprint.getStories());
            removedStories.removeAll(targetStoriesProperty.get());
            for (Story story : removedStories) {
                changes.add(new MoveItemCommand<>(story, sprint.getStories(), removedStories));
            }

            command = new CompoundCommand("Edit Sprint", changes);
        }
        return command;
    }

    public Property<String> teamNameProperty() {
        return teamNameProperty;
    }
    
    public Property<String> releaseNameProperty() {
        return releaseNameProperty;
    }
    
    public Property<String> backlogNameProperty() {
        return backlogNameProperty;
    }
}
