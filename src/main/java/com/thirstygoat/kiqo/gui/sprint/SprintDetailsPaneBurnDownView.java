package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.scrumBoard.DateAxis;
import com.thirstygoat.kiqo.model.Effort;
import com.thirstygoat.kiqo.model.Person;
import com.thirstygoat.kiqo.model.Task;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintDetailsPaneBurnDownView implements FxmlView<SprintDetailsPaneBurndownViewModel>, Initializable {

    @InjectViewModel
    private SprintDetailsPaneBurndownViewModel viewModel;

    @FXML
    private LineChart<LocalDate, Number> burndownChart;
    @FXML
    private Button button;
    @FXML
    private DateAxis xAxis;
    @FXML
    private NumberAxis yAxis;

    private XYChart.Series<LocalDate, Number> targetLineSeries = new XYChart.Series<>();
    private XYChart.Series<LocalDate, Number> loggedHoursSeries = new XYChart.Series<>();
    private XYChart.Series<LocalDate, Number> burndownSeries = new XYChart.Series<>();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        targetLineSeries.dataProperty().bind(viewModel.targetLineDataProperty());
        loggedHoursSeries.dataProperty().bind(viewModel.loggedHoursDataProperty());
        burndownSeries.dataProperty().bind(viewModel.burndownDataProperty());

        burndownChart.getData().addAll(targetLineSeries, loggedHoursSeries, burndownSeries);
        burndownChart.getXAxis().setAutoRanging(true);

        // style
        burndownChart.setLegendVisible(false);
        //        burndownChart.getXAxis().setTickLabelRotation(60); // need to have padding on the left because its uses the center


        yAxis.setMinorTickVisible(false);


        button.setOnAction(event -> {
//        	burndownChart.snapshot(snapshotResult -> {
//        		WritableImage image = snapshotResult.getImage();
//        		// https://community.oracle.com/thread/2450090?tstart=0
//        		final FileChooser fileChooser = new FileChooser();
//                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"), new FileChooser.ExtensionFilter("All Files", "*"));
//                String organisationFilename = viewModel.organisationProperty().get().getSaveLocation().getName();
//				fileChooser.setInitialFileName(organisationFilename.substring(0, organisationFilename.lastIndexOf('.')) + ".png");
//                File file = fileChooser.showSaveDialog(burndownChart.getScene().getWindow());
//        		try {
//					ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//        		return null;
//        	}, null, null);

            // Add some testing Effort data

            Task task = getViewModel().sprintProperty().get().getStories().get(0).getTasks().get(0);

            task.getLoggedEffort().clear();

            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 1, 0, 0), 3.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 3, 0, 0), 2.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 5, 0, 0), 4.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 6, 0, 0), 3.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 6, 0, 0), 5.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 7, 0, 0), 5.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 8, 0, 0), 1.0f, ""));
            task.getLoggedEffort().add(new Effort(new Person(), task, LocalDateTime.of(2015, 8, 12, 0, 0), 1.0f, ""));
            System.out.println(task.getLoggedEffort());

//            viewModel.draw();

        });

    }

    public SprintDetailsPaneBurndownViewModel getViewModel() {
        return viewModel;
    }
}
