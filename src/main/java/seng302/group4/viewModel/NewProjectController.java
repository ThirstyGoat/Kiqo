package seng302.group4.viewModel;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Bradley, James on 13/03/15.
 */
public class NewProjectController implements Initializable {
    private Stage stage;
    private File projectLocation;

    // FXML Injections
    @FXML
    private Button cancelButton;
    @FXML
    private Button newProjectButton;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField projectLocationTextField;
    @FXML
    private Button openButton;
    @FXML
    private TextField descriptionTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCancelButton();
        setOpenButton();
    }


    public File getProjectLocation() {
        return projectLocation;
    }

    /**
     * Sets the open dialog functionality including getting the path chosen by the user.
     */
    private void setOpenButton() {
        openButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(stage);
            projectLocation = selectedDirectory;

            projectLocationTextField.setText(selectedDirectory.getAbsolutePath());
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the cancel button functionality
     */
    private void setCancelButton() {
        cancelButton.setOnAction(event -> {
            stage.close();
        });
    }


}
