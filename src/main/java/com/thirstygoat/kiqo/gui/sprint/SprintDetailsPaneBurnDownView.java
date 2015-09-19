package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.LongBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneBurnDownView implements FxmlView<SprintDetailsPaneBurndownViewModel>, Initializable {

    @InjectViewModel
    private SprintDetailsPaneBurndownViewModel viewModel;

    @FXML
    private LineChart<Number, Number> burndownChart;
    @FXML
    private Button button;

    private XYChart.Series<Number, Number> targetLineSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> loggedHoursSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> burndownSeries = new XYChart.Series<>();

    private static int sum = 0;
    private static int day = 0;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        //TODO change X axis so that it shows dates
        burndownChart.getXAxis().setAutoRanging(false);
        viewModel.sprintProperty().addListener((observable, oldValue, newValue) -> {
            LongBinding dateRangeBinding = Bindings.createLongBinding(() ->
                    viewModel.sprintProperty().get().endDateProperty().get().toEpochDay() -
                            viewModel.sprintProperty().get().startDateProperty().get().toEpochDay()
                    , viewModel.sprintProperty().get().startDateProperty(), viewModel.sprintProperty().get().endDateProperty());

            targetLineSeries.getData().add(new XYChart.Data<>(0, 100));
            targetLineSeries.getData().add(new XYChart.Data<>(viewModel.sprintProperty().get().endDateProperty().get().toEpochDay() - viewModel.sprintProperty().get().startDateProperty().get().toEpochDay(), 0));
            ((NumberAxis) burndownChart.getXAxis()).upperBoundProperty().bind(dateRangeBinding);
        });

        burndownChart.getData().addAll(targetLineSeries, loggedHoursSeries, burndownSeries);

        //TODO remove (for testing purposes only
        button.setOnAction(event -> {
            loggedHoursSeries.getData().add(new XYChart.Data<>(day, sum));
            sum += 10;
            day += 1;
        });
    }

    public SprintDetailsPaneBurndownViewModel getViewModel() {
        return viewModel;
    }
}
