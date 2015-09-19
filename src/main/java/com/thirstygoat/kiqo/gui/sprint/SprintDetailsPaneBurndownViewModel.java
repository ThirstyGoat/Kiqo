package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import com.thirstygoat.kiqo.model.Sprint;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.XYChart;



public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {

    private ObjectProperty<XYChart.Series<Number, Number>> targetLineSeriesProperty = new SimpleObjectProperty<>();
    private static int sum = 0;

    public SprintDetailsPaneBurndownViewModel() {
        super();
    }

    public void addData() {
        targetLineSeriesProperty.get().getData().add(new XYChart.Data<>(sum, 10));
        sum += 10;
    }

    @Override
    public void commitEdit() {

    }

    @Override
    public void cancelEdit() {

    }

}


