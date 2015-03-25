package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import seng302.group4.Project;

/**
 * Created by Carina on 25/03/2015.
 */
public class ProjectDetailsPaneController implements DetailsPaneController<Project> {
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label projectLocationLabel;
    @FXML
    private Tooltip projectLocationTooltip;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Button editButton;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // disconnect tooltip if blank
        updateTooltip();
    }

    @Override
    public void showDetails(final Project project) {
        if (project != null) {
            shortNameLabel.setText(project.getShortName());
            longNameLabel.setText(project.getLongName());
            projectLocationLabel.setText(project.getSaveLocation().getAbsolutePath());
            descriptionLabel.setText(project.getDescription());
        } else {
            shortNameLabel.setText(null);
            longNameLabel.setText(null);
            projectLocationLabel.setText(null);
            descriptionLabel.setText(null);
        }
        updateTooltip();
    }

    /**
     * Attaches or detaches the tooltip for projectLocation based on whether the
     * tooltip would be empty.
     */
    private void updateTooltip() {
        projectLocationLabel.setTooltip(projectLocationTooltip.getText().isEmpty() ? null : projectLocationTooltip);
    }
}
