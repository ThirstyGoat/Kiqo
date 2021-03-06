package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

import com.thirstygoat.kiqo.search.SearchableField;


/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria extends Item {

    public final StringProperty criteria; // acts as shortName
    public final ObjectProperty<State> state;
    public final ObjectProperty<Story> story;

    public AcceptanceCriteria() {
        this.criteria = new SimpleStringProperty("");
        this.state = new SimpleObjectProperty<>(State.NEITHER);
        this.story = new SimpleObjectProperty<>(null);
    }

    public AcceptanceCriteria(String criteria, Story story) {
        this();
        setCriteria(criteria);
        setStory(story);
    }

    public static Callback<AcceptanceCriteria, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty(), p.state};
    }
    
    @Override
    public void initBoundPropertySupport() {
        bps.addPropertyChangeSupportFor(criteria);
        bps.addPropertyChangeSupportFor(state);
        bps.addPropertyChangeSupportFor(story);
    }

    public Story getStory() {
        return story.get();
    }

    public void setStory(Story story) {
        this.story.set(story);
    }

    public String getCriteria() {
        return criteria.get();
    }

    /**
     * For introspection
     * @param criteria Criteria to be set
     */
    // for introspection
    public void setCriteria(String criteria) {
        this.criteria.setValue(criteria);
    }

    public State getState() {
        return state.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    @Override
    public String toString() {
        return "AcceptanceCriteria{" 
                + "criteria=" + criteria.get() 
                + "state=" + state.get() 
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AcceptanceCriteria that = (AcceptanceCriteria) obj;
        return criteria.get().equals(that.criteria.get());
    }

    @Override
    public int hashCode() {
        return criteria.hashCode();
    }

    @Override
    public String getShortName() {
        return getCriteria();
    }

    @Override
    public StringProperty shortNameProperty() {
        return criteria;
    }

    @Override
    public List<SearchableField> getSearchableStrings() {
        List<SearchableField> list = new ArrayList<>();
        list.add(new SearchableField("Criteria", getCriteria()));
        return list;
    }

    /**
     * Represents the state of acceptance.
     */
    public enum State {
        ACCEPTED,
        REJECTED,
        NEITHER
    }
}
