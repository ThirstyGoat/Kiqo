package com.thirstygoat.kiqo.gui.sprint;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
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

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        targetLineSeries.dataProperty().bind(viewModel.targetLineDataProperty());
        loggedHoursSeries.dataProperty().bind(viewModel.loggedHoursDataProperty());
        burndownSeries.dataProperty().bind(viewModel.burndownDataProperty());

        burndownChart.getData().addAll(targetLineSeries, loggedHoursSeries, burndownSeries);
        burndownChart.getXAxis().setAutoRanging(true);

    }

    public SprintDetailsPaneBurndownViewModel getViewModel() {
        return viewModel;
    }
}
