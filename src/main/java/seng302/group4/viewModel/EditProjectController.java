package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import seng302.group4.Project;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
        this.setCancelButton();
        this.setSaveButton();
    }

    /**
     * Populates the fields with project data to enable editing
     *
     * @param project
     */
    public void loadProject(final Project project) {
        this.project = project;
        this.formController.loadProject(project);
    }

    /**
     * Sets the event handler for the Save button, performs validation checks
     * and instantiates the new project if applicable
     */
    private void setSaveButton() {
        this.editProjectButton.setOnAction(event -> {
            this.formController.validate();
            if (this.formController.isValid()) {
                final ArrayList<Command<?>> changes = new ArrayList<>();

                if (!this.formController.longName.equals(this.project.getLongName())) {
                    changes.add(new EditCommand<>(this.project, "longName", this.formController.longName));
                }
                if (!this.formController.shortName.equals(this.project.getShortName())) {
                    changes.add(new EditCommand<>(this.project, "shortName", this.formController.shortName));
                }
                if (!this.formController.projectLocation.equals(this.project.getSaveLocation())) {
                    changes.add(new EditCommand<>(this.project, "saveLocation", this.formController.projectLocation));
                }
                if (!this.formController.description.equals(this.project.getDescription())) {
                    changes.add(new EditCommand<>(this.project, "description", this.formController.description));
                }

                this.valid = !changes.isEmpty();
                // TODO possibly no changes, create command anyway?
                this.command = new CompoundCommand(changes);

                // Close the new project dialog (this window)
                this.stage.close();
            }
        });
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * @return the command
     */
    public CompoundCommand getCommand() {
        return this.command;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    private void setCancelButton() {
        this.cancelButton.setOnAction(event -> this.stage.close());
    }
}
