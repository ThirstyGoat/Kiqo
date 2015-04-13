package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import seng302.group4.Person;
import seng302.group4.Release;
import seng302.group4.Team;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ReleaseDetailsPaneController implements Initializable {
    @FXML
    private Label IDLabel;
    @FXML
    private Label releaseDateLabel;
    @FXML
    private Label descriptionLabel;


    public void showDetails(final Release release) {
        if (release != null) {
            IDLabel.setText(release.getId());
            releaseDateLabel.setText(release.getDate());
            descriptionLabel.setText(release.getDescription());

        } else {
            IDLabel.setText(null);
            releaseDateLabel.setText(null);
            descriptionLabel.setText(null);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
