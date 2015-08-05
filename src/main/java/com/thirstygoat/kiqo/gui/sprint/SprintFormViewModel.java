package com.thirstygoat.kiqo.gui.sprint;

import javafx.beans.binding.BooleanExpression;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.util.StringConverters;


/**
 * Created by samschofield on 31/07/15.
 */
public class SprintFormViewModel extends SprintViewModel implements IFormViewModel<Sprint> {
    
    private StringProperty teamNameProperty;
    private StringProperty releaseNameProperty;
    private StringProperty backlogNameProperty;
    
    public SprintFormViewModel() {
        super();
        teamNameProperty = new SimpleStringProperty("");
        releaseNameProperty = new SimpleStringProperty("");
        backlogNameProperty = new SimpleStringProperty("");
    }
    
    @Override
    public void load(Sprint sprint, Organisation organisation) {
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
            super.getStories().clear();
            super.getStories().addAll(sprint.getStories());
        } else {
            super.goalProperty().set(null);
            super.longNameProperty().set(null);
            super.descriptionProperty().set(null);
            super.backlogProperty().set(null);
            super.startDateProperty().set(null);
            super.endDateProperty().set(null);
            super.teamProperty().set(null);
            super.releaseProperty().set(null);
            super.getStories().clear();
        }
    }
    
    @Override
    public Command<?> createCommand() {
        // TODO implement me
        final Command<Void> noop = new Command<Void>() {

            @Override
            public Void execute() {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void undo() {
                // TODO Auto-generated method stub
            }

            @Override
            public String getType() {
                return "no-op";
            }
        };
        return noop;
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
