package com.thirstygoat.kiqo.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria implements Serializable {

    public final StringProperty criteria;


    public AcceptanceCriteria(String criteria) {
        this.criteria = new SimpleStringProperty(criteria);
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

}
