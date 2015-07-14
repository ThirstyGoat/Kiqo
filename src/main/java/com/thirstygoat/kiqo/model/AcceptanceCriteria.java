package com.thirstygoat.kiqo.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria extends Item {

    public final StringProperty criteria;
    public final ObjectProperty<State> state;

    public AcceptanceCriteria(String criteria) {
        this.criteria = new SimpleStringProperty(criteria);
        this.state = new SimpleObjectProperty(State.NEITHER);
    }

    /**
     * For introspection
     * @param criteria
     */
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
        return "AcceptanceCriteria{" +
                "criteria=" + criteria.get() +
                "state=" + state.get() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AcceptanceCriteria that = (AcceptanceCriteria) o;
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
     * Enum for the state of the acceptance criteria
     */
    public enum State {
        ACCEPTED("Accepted"),
        REJECTED("Rejected"),
        NEITHER("Neither");

        private String label;

        State(String label) {
            this.label = label;
        }

        /**
         * Converts the string to uppercase so that it will match the enum we are looking for
         * so we can take the string value from the combo box used for setting state
         */
        public static State getEnum(String val) {
            return State.valueOf(val.toUpperCase());
        }

        /**
         * Used so the combo box can be filled more easily
         * @return Arraylist of filled with the labels for each enum
         */
        public static ArrayList<String> getStringValues() {
            ArrayList<String> vals = new ArrayList<>();
            for (State state : State.values()) {
                vals.add(state.toString());
            }
            return vals;
        }

        public String toString() {
            return label;
        }
    }
}
