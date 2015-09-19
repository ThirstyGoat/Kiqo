package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import java.time.LocalDate;


public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {
    private ObservableList<XYChart.Data<Number, Number>> targetLineData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> targetLineDataProperty = new SimpleObjectProperty<>(targetLineData);
    private ObservableList<XYChart.Data<Number, Number>> loggedHoursData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> loggedHoursDataProperty = new SimpleObjectProperty<>(loggedHoursData);
    private ObservableList<XYChart.Data<Number, Number>> burndownData = FXCollections.observableArrayList();
    private ObjectProperty<ObservableList<XYChart.Data<Number, Number>>> burndownDataProperty = new SimpleObjectProperty<>(burndownData);

    public SprintDetailsPaneBurndownViewModel() {
        super();
        sprintWrapper.dirtyProperty().addListener((observable, oldValue, newValue) -> {
            drawTargetLine();
            drawBurndownLine();
        });

    }

    private void drawBurndownLine() {
        burndownData.clear();
        long days = LocalDate.now().toEpochDay() - sprintProperty().get().startDateProperty().get().toEpochDay();
        for (int i = 0; i < days; i++) {
            burndownData.add(new XYChart.Data<>(i, Math.max(sprintProperty().get().totalSprintEstimate()
                    - sprintProperty().get().hoursLoggedAtDay(i), 0)));
        }
    }

    private void drawTargetLine() {
        targetLineData.clear();
        targetLineData.add(new XYChart.Data<>(0, sprintProperty().get().totalSprintEstimate()));
        targetLineData.add(new XYChart.Data<>(
                sprintProperty().get().endDateProperty().get().toEpochDay()
                        - sprintProperty().get().startDateProperty().get().toEpochDay()
                , 0));
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

    @Override
    public void commitEdit() {
        // no editing done on chart
    }

    @Override
    public void cancelEdit() {
        // no editing done on chart
    }

}


