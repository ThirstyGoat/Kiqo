package com.thirstygoat.kiqo.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria extends Item {

    public final StringProperty criteria;
    public final ObjectProperty<State> state;

    public AcceptanceCriteria(String criteria) {
        this.criteria = new SimpleStringProperty(criteria);
        this.state = new SimpleObjectProperty<>(State.NEITHER);
    }

    // for introspection
    public void setCriteria(String criteria) {
        this.criteria.setValue(criteria);
    }

    public String getCriteria() {
        return criteria.get();
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public State getState() {
        return state.get();
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
        return criteria.getName();
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
        NEITHER;
    }
}
