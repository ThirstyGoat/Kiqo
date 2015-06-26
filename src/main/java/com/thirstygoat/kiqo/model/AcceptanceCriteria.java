package com.thirstygoat.kiqo.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria {

    public StringProperty criteria;
    public BooleanProperty done;

    public AcceptanceCriteria(String critera) {
        this.criteria = new SimpleStringProperty(critera);
        this.done = new SimpleBooleanProperty(false);
    }

    public void setCriteria(String criteria) {
        this.criteria.setValue(criteria);
    }

}
