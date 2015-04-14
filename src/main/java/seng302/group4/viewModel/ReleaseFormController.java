package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import seng302.group4.Project;
import seng302.group4.Release;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.CreateReleaseCommand;
import seng302.group4.undo.EditCommand;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


/**
 * Created by james on 11/04/15.
 */
public class ReleaseFormController implements Initializable {
    private final PopOver errorPopOver = new PopOver();
    private Project project;
    private Release release;
    private Command command;
    private boolean valid = false;

    // Begin FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Stage stage;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setTextFieldListener();
//        setShortNameLengthRestrictor();  // need to discuss if we are going with ID, or shortname or both

        Platform.runLater(shortNameTextField::requestFocus);
    }

    private void setShortNameLengthRestrictor() {

    }

    private void setTextFieldListener() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                errorPopOver.hide();
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                errorPopOver.hide();
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }



    private boolean validate() {
        //TODO
        valid = true;
        setCommand();
        return true;
    }

    public boolean isValid() {
        return valid;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand() {
        if (release == null) {
            // new release command
            command = new CreateReleaseCommand(shortNameTextField.getText(), releaseDatePicker.getValue(),
                    descriptionTextField.getText(), project);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!shortNameTextField.getText().equals(release.getShortName())) {
                changes.add(new EditCommand<>(release, "shortName", shortNameTextField.getText()));
            }
            if (!releaseDatePicker.getValue().equals(release.getDate())) {
                changes.add(new EditCommand<>(release, "date", releaseDatePicker.getValue()));
            }
            if (!descriptionTextField.getText().equals(release.getDescription())) {
                changes.add(new EditCommand<>(release, "description", descriptionTextField.getText()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand(changes);
        }
    }



    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setRelease(Release release) {
        this.release = release;

        if (release == null) {
            // create a release
            stage.setTitle("Create Release");
            okButton.setText("Create Release");
        } else {
            // edit an existing release
            stage.setTitle("Edit Release");
            okButton.setText("Save");

            shortNameTextField.setText(release.getShortName());
            releaseDatePicker.setValue(release.getDate());
            descriptionTextField.setText(release.getDescription());
        }
    }

}
