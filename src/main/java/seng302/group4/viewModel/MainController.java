package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.stage.*;
import seng302.group4.PersistenceManager;
import seng302.group4.Project;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import java.io.File;

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
    @FXML
    private MenuItem projectDetailsMenuItem;

    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private Project selectedProject;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuitMenuItem();
        setListToggleCheckMenuItem();
        setLayoutProperties();
        setNewProjectMenuItem();
        setProjectDetailsMenuItem();

        setMainListView();
        setOpenMenu();
        setSaveMenu();
        setShortcuts();
    }

    private void setProjectDetailsMenuItem() {
        projectDetailsMenuItem.setOnAction(event -> {
            if (selectedProject != null) {
                editProjectDialog(selectedProject);
            } else {
                // Something went wrong and the button wasn't disabled, alert the user
                // TODO
            }
        });
    }

    private void refreshList() {
        mainListView.setItems(null);
        mainListView.setItems(projects);
    }

    private void setSaveMenu() {
        saveMenuItem.setOnAction(event -> {
            try {
                PersistenceManager.saveProject(selectedProject.getSaveLocation().getAbsoluteFile(), selectedProject);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the shortcuts for the main window
     */
    private void setShortcuts() {
        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));
    }

    /**
     * Sets the handler so an open dialog is presented when the user clicks File->Open
     */
    private void setOpenMenu() {
        openMenuItem.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
            File filePath = fileChooser.showOpenDialog(primaryStage);

            // TODO Actually do something with the selected file
            try {
                Project project = PersistenceManager.loadProject(filePath);
                projects.add(project);
                System.out.println(project.toString() + " has been loaded successfully");
            } catch (Exception e) {
                System.out.println("Couldnt load project");
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the content for the main list view
     */
    private void setMainListView() {
        mainListView.setItems(projects);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editContextMenu = new MenuItem("Edit Project");
        contextMenu.getItems().add(editContextMenu);

        mainListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> {
            if (selectedProject != null) {
                editProjectDialog(selectedProject);
            }
        });

        // Set change listener for mainListView
        mainListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedProject = newValue;
            if (newValue != null) {
                // Then a project is selected, enable the Project Details MenuItem
                projectDetailsMenuItem.setDisable(false);
            } else {
                // No project selected, disable Project Details MenuItem
                projectDetailsMenuItem.setDisable(true);
            }
        });
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

    private void editProjectDialog(Project project) {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Edit Project");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/editProject.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            EditProjectController editProjectController = loader.getController();
            editProjectController.setStage(stage);
            editProjectController.loadProject(project);

            stage.showAndWait();
            refreshList();
        });
    }

    private void newProjectDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
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
