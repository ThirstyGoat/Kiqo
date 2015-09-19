package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.Editable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.chart.XYChart;


/**
 * Created by Carina Blair on 5/08/2015.
 */
public class SprintDetailsPaneBurndownViewModel extends SprintViewModel implements Editable {

    private ObjectProperty<XYChart.Series<Number, Number>> targetLineSeriesProperty = new SimpleObjectProperty<>();
    private static int sum = 0;
    private SprintDetailsPaneBurndownViewModel viewModel;

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


