package seng302.group4.viewModel;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import seng302.group4.Project;
import seng302.group4.undo.CreateProjectCommand;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class NewProjectController implements Initializable {
    private Stage stage;
    private Project project;
    private boolean valid = false;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newProjectButton;
    @FXML
    private ProjectFormController formController;

    private CreateProjectCommand command;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.setCancelButton();
        this.setNewButton();
    }

    /**
     * Sets the event handler for the New Project Button, performs validation
     * checks and instantiates the new project if applicable
     */
    private void setNewButton() {
        this.newProjectButton.setOnAction(event -> {
            this.formController.validate();
            if (this.formController.isValid()) {
                this.command = new CreateProjectCommand(this.formController.shortName, this.formController.longName,
                        this.formController.projectLocation, this.formController.description);
                this.valid = true;
                // Close the new project dialog (this window)
                this.stage.close();
            }
        });
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        this.cancelButton.setOnAction(event -> this.stage.close());
    }

    /**
     * @return the formController
     */
    public ProjectFormController getFormController() {
        return this.formController;
    }

    /**
     * @return the command
     */
    public CreateProjectCommand getCommand() {
        return this.command;
    }

    /**
     * @return validity of all fields
     */
    public boolean isValid() {
        return this.valid;
    }
}
