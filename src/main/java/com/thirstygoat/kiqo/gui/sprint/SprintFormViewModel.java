package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.model.Sprint;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.StringConverters;

import javafx.beans.property.*;
import javafx.collections.ObservableList;


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
    public void bindStringProperties() {
        backlogShortNameProperty.bindBidirectional(super.backlogProperty(),
                StringConverters.backlogStringConverter(super.organisation));
        teamShortNameProperty.bindBidirectional(super.teamProperty(),
                StringConverters.teamStringConverter(super.organisation));
        releaseShortNameProperty.bindBidirectional(super.releaseProperty(),
                StringConverters.releaseStringConverter(super.organisation));
    }

    @Override
    public void setExitStrategy(Runnable exitStrategy) {
        canceled.addListener((observable) -> {
                exitStrategy.run();
        });
    }

    public void okAction() {
        canceled.set(true); // Set true first to trigger change on listener.
        canceled.set(false);
    }

    public void cancelAction() {
        canceled.set(true);
    }

    public Boolean isCanceled() {
        return canceled.get();
    }

    public StringProperty backlogShortNameProperty() {
        return backlogShortNameProperty;
    }

    public StringProperty teamShortNameProperty() {
        return teamShortNameProperty;
    }

    public StringProperty releaseShortNameProperty() {
        return releaseShortNameProperty;
    }
}
