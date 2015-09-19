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
    private static int sum = 0;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        viewModel.sprintProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(viewModel.sprintProperty().getValue().startDateProperty().getValue().getDayOfMonth());

        });

        button.setOnAction(event -> {
            targetLineSeries.getData().add(new XYChart.Data<>(sum, 10));
            sum += 10;
        });

        burndownChart.getXAxis().setAutoRanging(true);
        burndownChart.getData().add(targetLineSeries);
    }

    public SprintDetailsPaneBurndownViewModel getViewModel() {
        return viewModel;
    }
}
