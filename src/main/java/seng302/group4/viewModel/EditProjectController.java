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

    private Organisation organisation;

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
     * @param organisation source of existing field data
     */
    public void loadProject(final Organisation organisation) {
        this.organisation = organisation;
        formController.loadProject(organisation);
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

                if (!formController.longName.equals(organisation.getLongName())) {
                    changes.add(new EditCommand<>(organisation, "longName", formController.longName));
                }
                if (!formController.shortName.equals(organisation.getShortName())) {
                    changes.add(new EditCommand<>(organisation, "shortName", formController.shortName));
                }
                if (!formController.projectLocation.equals(organisation.getSaveLocation())) {
                    changes.add(new EditCommand<>(organisation, "saveLocation", formController.projectLocation));
                }
                if (!formController.description.equals(organisation.getDescription())) {
                    changes.add(new EditCommand<>(organisation, "description", formController.description));
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

    public Organisation getOrganisation() {
        return organisation;
    }

    public CompoundCommand getCommand() {
        return command;
    }

    public boolean isValid() {
        return valid;
    }
}
