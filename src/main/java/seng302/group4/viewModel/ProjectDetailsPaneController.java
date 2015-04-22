package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Organisation;

/**
 * Created by Carina on 25/03/2015.
 */
public class ProjectDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label projectLocationLabel;
    @FXML
    private Label descriptionLabel;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    }

    public void showDetails(final Organisation organisation) {
        if (organisation != null) {
            shortNameLabel.setText(organisation.getShortName());
            longNameLabel.setText(organisation.getLongName());
            projectLocationLabel.setText(organisation.getSaveLocation().getAbsolutePath());
            descriptionLabel.setText(organisation.getDescription());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            projectLocationLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }
}
