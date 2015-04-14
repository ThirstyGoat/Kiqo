package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Release;

import java.net.URL;
import java.util.ResourceBundle;

public class ReleaseDetailsPaneController implements Initializable {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label releaseDateLabel;
    @FXML
    private Label descriptionLabel;


    public void showDetails(final Release release) {
        if (release != null) {
            shortNameLabel.setText(release.getShortName());
            releaseDateLabel.setText(release.getDate().toString());
            descriptionLabel.setText(release.getDescription());

        } else {
            shortNameLabel.setText(null);
            releaseDateLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
