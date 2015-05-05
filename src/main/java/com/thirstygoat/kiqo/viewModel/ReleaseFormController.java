package com.thirstygoat.kiqo.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.TextFields;

import com.thirstygoat.kiqo.Organisation;
import com.thirstygoat.kiqo.Project;
import com.thirstygoat.kiqo.Release;
import com.thirstygoat.kiqo.undo.Command;
import com.thirstygoat.kiqo.undo.CompoundCommand;
import com.thirstygoat.kiqo.undo.CreateReleaseCommand;
import com.thirstygoat.kiqo.undo.EditCommand;
import com.thirstygoat.kiqo.undo.MoveItemCommand;


/**
 * Created by james on 11/04/15.
 */
public class ReleaseFormController implements Initializable {
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final PopOver errorPopOver = new PopOver();
    private Organisation organisation;
    private Release release;
    private Command command;
    private boolean valid = false;

    // Begin FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private DatePicker releaseDatePicker;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private Project project;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setShortNameTextFieldListener();
        setShortNameLengthRestrictor();
        setProjectTextFieldSuggester();
        setReleaseDateChecker();
        Platform.runLater(shortNameTextField::requestFocus);
    }

    /**
     * Sets up a listener on the name field of team to restrict it to the predefined maximum length
     */
    private void setShortNameLengthRestrictor() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
                errorPopOver.setContentNode(new Label("Short name must be under " + SHORT_NAME_MAX_LENGTH +
                        " characters"));
                errorPopOver.show(shortNameTextField);
            }
        });
    }

    private void setShortNameTextFieldListener() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            errorPopOver.hide();
        });
    }

    private void setProjectTextFieldSuggester() {
        // use a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
        // use a String converter so that the Project's short name is used.
        TextFields.bindAutoCompletion(projectTextField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Project>>() {
            @Override
            public Collection<Project> call(ISuggestionRequest request) {
                // TODO filter based on input string
                // if (project.contains(request.getUserText()) { /* keep it */ }
                return organisation.getProjects();
            }

        }, new StringConverter<Project>() {
            @Override
            public Project fromString(String string) {
                for (final Project project : organisation.getProjects()) {
                    if (project.getShortName().equals(string)) {
                        return project;
                    }
                }
                return null;
            }

            @Override
            public String toString(Project project) {
                return project.getShortName();
            }
        });
    }

    private void setReleaseDateChecker() {
        final String dateRegX = "(\\d|\\d\\d)/(\\d|\\d\\d)/\\d\\d\\d\\d";

        releaseDatePicker.getEditor().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                if (!releaseDatePicker.getEditor().getText().matches(dateRegX)) {
                    errorPopOver.setContentNode(new Label("Valid release date required"));
                    errorPopOver.show(releaseDatePicker);
                    releaseDatePicker.getEditor().setText("");
                } else {
                    errorPopOver.hide();
                }
            } else {
                errorPopOver.hide();
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                errorPopOver.hide(Duration.millis(0));
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    private boolean validate() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }

        if (projectTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Project must not be empty"));
            errorPopOver.show(projectTextField);
            return false;
        } else {
            // check that text matches an existing shortName (and keep a reference to the matching project)
            project = null;
            for (final Project p : organisation.getProjects()) {
                if (projectTextField.getText().equals(p.getShortName())) {
                    project = p;
                    break;
                }
            }
            if (project == null) {
                errorPopOver.setContentNode(new Label("Project \"" + projectTextField.getText() + "\" does not exist"));
                errorPopOver.show(projectTextField);
                return false;
            }
        }

        if (releaseDatePicker.getValue() == null) {
            errorPopOver.setContentNode(new Label("Valid release date required"));
            errorPopOver.show(releaseDatePicker);
            return false;
        }

        if (release != null) {
            // we're editing
            if (shortNameTextField.getText().equals(release.getShortName())) {
                // then that's fine
                valid = true;
                setCommand();
                return true;
            }
        }
        for (final Release r : project.getReleases()) {
            if (shortNameTextField.getText().equals(r.getShortName())) {
                errorPopOver.setContentNode(new Label("Short name must be unique"));
                errorPopOver.show(shortNameTextField);
                return false;
            }
        }
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
            release = new Release(shortNameTextField.getText(), project, releaseDatePicker.getValue(),
                    descriptionTextField.getText(), organisation);
            command = new CreateReleaseCommand(release, organisation);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!shortNameTextField.getText().equals(release.getShortName())) {
                changes.add(new EditCommand<>(release, "shortName", shortNameTextField.getText()));
            }
            if (!project.equals(release.getProject())) {
                changes.add(new MoveItemCommand(release, (Collection) release.getProject().getReleases(), (Collection) project.getReleases()));
                changes.add(new EditCommand<>(release, "project", project));
            }
            if (!releaseDatePicker.getValue().equals(release.getDate())) {
                changes.add(new EditCommand<>(release, "date", releaseDatePicker.getValue()));
            }
            if (!descriptionTextField.getText().equals(release.getDescription())) {
                changes.add(new EditCommand<>(release, "description", descriptionTextField.getText()));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Release", changes);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    public void setRelease(Release release) {
        this.release = release;

        if (release == null) {
            // create a release
            stage.setTitle("Create Release");
            okButton.setText("Create Release");
            releaseDatePicker.setPromptText("dd/mm/yyyy");
        } else {
            // edit an existing release
            stage.setTitle("Edit Release");
            okButton.setText("Save");

            shortNameTextField.setText(release.getShortName());
            projectTextField.setText(release.getProject().getShortName());
            releaseDatePicker.setValue(release.getDate());
            descriptionTextField.setText(release.getDescription());
        }
    }
}