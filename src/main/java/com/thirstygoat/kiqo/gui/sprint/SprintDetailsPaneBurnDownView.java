package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.scrumBoard.DateAxis;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
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
    private Hyperlink exportHyperlink;
    @FXML
    private DateAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private VBox burndownVBox;

    private XYChart.Series<LocalDate, Number> targetLineSeries = new XYChart.Series<>();
    private XYChart.Series<LocalDate, Number> loggedHoursSeries = new XYChart.Series<>();
    private XYChart.Series<LocalDate, Number> burndownSeries = new XYChart.Series<>();

    private LongProperty days = new SimpleLongProperty();

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        days.bind(viewModel.daysProperty());
        targetLineSeries.dataProperty().bind(viewModel.targetLineDataProperty());
        loggedHoursSeries.dataProperty().bind(viewModel.loggedHoursDataProperty());
        burndownSeries.dataProperty().bind(viewModel.burndownDataProperty());

        burndownChart.getData().addAll(targetLineSeries, loggedHoursSeries, burndownSeries);
        burndownChart.getXAxis().setAutoRanging(true);

        // style
        burndownChart.setLegendVisible(false);
        burndownChart.setAnimated(false);


        yAxis.setMinorTickVisible(false);

        exportHyperlink.setOnAction(event -> {
            burndownVBox.snapshot(snapshotResult -> {
                WritableImage image = snapshotResult.getImage();
                // https://community.oracle.com/thread/2450090?tstart=0
                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                                new FileChooser.ExtensionFilter("All Files", "*"));
                String organisationFilename = viewModel.organisationProperty().get().getSaveLocation().getName();
                fileChooser.setInitialFileName(
                                organisationFilename.substring(0, organisationFilename.lastIndexOf('.')) + ".png");
                File file = fileChooser.showSaveDialog(burndownChart.getScene().getWindow());
                try {
                    if (file != null) {
                        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }, null, null);
        });

    }

    public SprintDetailsPaneBurndownViewModel getViewModel() {
        return viewModel;
    }
}
