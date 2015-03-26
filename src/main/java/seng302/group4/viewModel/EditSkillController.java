package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.EditCommand;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditSkillController implements Initializable {
    private Stage stage;
    private Project project;
    private Skill skill;
    private boolean valid = false;
    private CompoundCommand command;
    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private SkillFormController formController;


    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.setCancelButton();
        this.setSaveButton();

        Platform.runLater(() -> setProjectForFormController());
    }

    /**
     * Sets the event handler for the Save button, performs validation checks
     * and instantiates the new project if applicable
     */
    private void setSaveButton() {
        saveButton.setOnAction(event -> {
            // check to see that shortname is populate and is unique within the project
            formController.validate();
            if (formController.isValid()) {

                final ArrayList<Command<?>> changes = new ArrayList<>();

                if (!formController.getShortName().equals(skill.getShortName())) {
                    for (Skill s : project.getSkills()) {
                        if (formController.getShortName().equals(s.getShortName())) {
                            formController.warnShortnameNotUnique();
                            return;
                        }
                    }
                    changes.add(new EditCommand<>(skill, "shortName", formController.getShortName()));
                }
                if (!formController.getDescription().equals(skill.getDescription())) {
                    changes.add(new EditCommand<>(skill, "description", formController.getDescription()));
                }

                valid = !changes.isEmpty();

                command = new CompoundCommand(changes);
                // Close the new project dialog (this window)
                this.stage.close();
            }
        });
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void loadSkill(Skill skill) {
        this.skill = skill;
    }

    public CompoundCommand getCommand() {
        return command;
    }

    /**
     * @return the valid
     */
    public boolean isValid() {
        return this.valid;
    }

    public void setStage(final Stage stage) {
        this.stage = stage;
    }

    private void setCancelButton() {
        this.cancelButton.setOnAction(event -> this.stage.close());
    }

    public void setProjectForFormController() {
        formController.setProject(project);
        formController.loadSkill(skill);
    }
}
