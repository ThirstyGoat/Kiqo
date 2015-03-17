package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import seng302.group4.PersistenceManager;
import seng302.group4.Project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private Stage primaryStage;
    private AnchorPane listAnchorPane;
    private double dividerPosition;


    // FXML Injections
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private ListView<Project> mainListView;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem openMenuItem;
    @FXML
    private MenuItem saveMenuItem;

    private ObservableList<Project> projects = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuitMenuItem();
        setListToggleCheckMenuItem();
        setLayoutProperties();
        setNewProjectMenuItem();

        setMainListView();
        setOpenMenu();
        setShortcuts();
    }

    /**
     * Sets the shortcuts for the main window
     */
    private void setShortcuts() {
        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
    }

    /**
     * Sets the handler so an open dialog is presented when the user clicks File->Open
     */
    private void setOpenMenu() {
        openMenuItem.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.showDialog(primaryStage);
            // TODO Actually do something with the selected file
        });
    }

    /**
     * Sets the content for the main list view
     */
    private void setMainListView() {
        mainListView.setItems(projects);
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        listAnchorPane = (AnchorPane) mainSplitPane.getItems().get(0);

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Sets the functionality for the quit menu item
     */
    private void setQuitMenuItem() {
        quitMenuItem.setOnAction(event -> {
            primaryStage.close();
        });
    }

    /**
     * Sets the functionality for the toggle list view menu item
     */
    private void setListToggleCheckMenuItem() {
        listToggleCheckMenuItem.setSelected(true);
        listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // shows the list view
                mainSplitPane.getItems().add(0, listAnchorPane);
                mainSplitPane.setDividerPosition(0, dividerPosition);
            } else {
                // hides the list view
                dividerPosition = mainSplitPane.getDividerPositions()[0];
                mainSplitPane.getItems().remove(listAnchorPane);
            }

        });
    }

    private void setNewProjectMenuItem() {
        newProjectMenuItem.setOnAction(event -> {
            System.out.println("Called newProject");
            newProjectDialog();
        });
    }

    private void newProjectDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newProject.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            NewProjectController newProjectController = loader.getController();
            newProjectController.setStage(stage);

            stage.showAndWait();
            addProject(newProjectController.getProject());
        });

    }

    /**
     * Adds the new project to the observable list so that it is visible in the list view
     * @param project New Project to be added
     */
    private void addProject(Project project) {
        if (project != null) {
            projects.add(project);
            try {
                PersistenceManager.saveProject(project.getSaveLocation(), project);
            } catch (IOException e) {
                System.out.println("Could not save project");
            }

        }
    }
}
