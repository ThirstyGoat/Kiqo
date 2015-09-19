package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;



public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {
    private ObservableList<XYChart.Data<Number, Number>> targetLineData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> targetLineDataProperty = new SimpleObjectProperty<>(targetLineData);
    private ObservableList<XYChart.Data<Number, Number>> loggedHoursData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> loggedHoursDataProperty = new SimpleObjectProperty<>(loggedHoursData);
    private ObservableList<XYChart.Data<Number, Number>> burndownData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> burndownDataProperty = new SimpleObjectProperty<>(burndownData);


    public SprintDetailsPaneBurndownViewModel() {
        super();
    }

    public ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> targetLineDataProperty() {
        return targetLineDataProperty;
    }

    public ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> loggedHoursDataProperty() {
        return loggedHoursDataProperty;
    }

    public ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> burndownDataProperty() {
        return burndownDataProperty;
    }

    //TODO remove this
    public void buttonPress() {
        targetLineData.add(new XYChart.Data(10, 20));
    }

    @Override
    public void commitEdit() {
        // no editing done on chart
    }

    @Override
    public void cancelEdit() {
        // no editing done on chart
    }

}


