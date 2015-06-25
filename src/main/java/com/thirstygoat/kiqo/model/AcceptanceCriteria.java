package com.thirstygoat.kiqo.model;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Arrays;
import java.util.List;

/**
 * Created by samschofield on 25/06/15.
 */
public class AcceptanceCriteria {

    public ObservableList<String> criteria;

    public AcceptanceCriteria() {
        criteria = FXCollections.observableArrayList();
        criteria.addAll(Arrays.asList("A", "B", "C"));
    }

}
