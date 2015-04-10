package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.util.Duration;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.undo.CreateSkillCommand;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by james on 18/03/15.
 */
public class NewSkillController implements Initializable {
    private Stage stage;
    private boolean valid = false;
    private Project project;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button addButton;
    @FXML
    private SkillFormController formController;

    private CreateSkillCommand command;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setAddButton();
        setCancelButton();

        Platform.runLater(() -> setProjectForFormController());
    }

    /**
     * Sets the cancel button for new Person dialog
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            formController.errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    /**
     * Sets the New Person button for new Person dialog
     */
    private void setAddButton() {
        addButton.setOnAction(event -> {
            // check to see that shortname
            formController.validate();
            if (formController.isValid()) {
                for (Skill s : project.getSkills()) {
                    if (formController.getShortName().equals(s.getShortName())) {
                        formController.warnShortnameNotUnique();
                        return;
                    }
                }
                Skill skill = new Skill(formController.getShortName(), formController.getDescription());
                command = new CreateSkillCommand(skill, project);

                valid = true;
                stage.close();
            }
        });
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public CreateSkillCommand getCommand() {
        return command;
    }

    private void setProjectForFormController() {
        formController.setProject(project);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * @return validity of all fields
     */
    public boolean isValid() {
        return valid;
    }
}
