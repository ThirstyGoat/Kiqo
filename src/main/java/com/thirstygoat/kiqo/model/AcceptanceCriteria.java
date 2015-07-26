package com.thirstygoat.kiqo.model;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria extends Item {

    public final StringProperty criteria;
    public final ObjectProperty<State> state;

    public AcceptanceCriteria() {
        this.criteria = new SimpleStringProperty("");
        this.state = new SimpleObjectProperty<>(State.NEITHER);
    }

    public AcceptanceCriteria(String criteria) {
        this.criteria = new SimpleStringProperty(criteria);
        this.state = new SimpleObjectProperty<>(State.NEITHER);
    }

    public static Callback<AcceptanceCriteria, Observable[]> getWatchStrategy() {
        return p -> new Observable[] {p.shortNameProperty(), p.state};
    }

    public String getCriteria() {
        return criteria.get();
    }

    /**
     * For introspection
     * @param criteria
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

    /**
     * Represents the state of acceptance.
     */
    public enum State {
        ACCEPTED,
        REJECTED,
        NEITHER
    }

    @Override
    public String[] getSearchableStrings() {
        return new String[] {};
    }
}
