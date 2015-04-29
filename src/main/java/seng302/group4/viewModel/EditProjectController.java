package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng302.group4.Organisation;
import seng302.group4.Project;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

/**
 * Created by Bradley on 13/03/15.
 */
public class EditProjectController implements Initializable {
    private Stage stage;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button editProjectButton;
    @FXML
    private ProjectFormController formController;

    private Project project;

    private boolean valid = false;
    private CompoundCommand command;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setCancelButton();
        setSaveButton();
    }

    /**
     * Populates the fields with project data to enable editing
     *
     * @param project source of existing field data
     */
    public void loadProject(final Project project) {
        this.project = project;
        formController.loadProject(project);
    }

    /**
     * Sets the event handler for the Save button, performs validation checks
     * and instantiates the new project if applicable
     */
    private void setSaveButton() {
        editProjectButton.setOnAction(event -> {
            formController.validate();
            if (formController.isValid()) {
                valid = true;
                final ArrayList<Command<?>> changes = new ArrayList<>();

                if (!formController.longName.equals(project.getLongName())) {
                    changes.add(new EditCommand<>(project, "longName", formController.longName));
                }
                if (!formController.shortName.equals(project.getShortName())) {
                    changes.add(new EditCommand<>(project, "shortName", formController.shortName));
                }
                if (!formController.description.equals(project.getDescription())) {
                    changes.add(new EditCommand<>(project, "description", formController.description));
                }

                command = new CompoundCommand("Edit Project", changes);

                // Close the new project dialog (this window)
                stage.close();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        formController.setStage(stage);
    }

    /**
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            formController.errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    public Project getProject() {
        return project;
    }

    public CompoundCommand getCommand() {
        return command;
    }

    public boolean isValid() {
        return valid;
    }
}