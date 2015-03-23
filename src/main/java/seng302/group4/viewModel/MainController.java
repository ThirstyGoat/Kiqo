package seng302.group4.viewModel;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.controlsfx.control.StatusBar;

import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import javafx.stage.WindowEvent;
import seng302.group4.PersistenceManager;
import seng302.group4.Project;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.CreateProjectCommand;
import seng302.group4.undo.UndoManager;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private Stage primaryStage;
    private AnchorPane listAnchorPane;
    private double dividerPosition;

    // FXML Injections
    @FXML
    private BorderPane mainBorderPane;
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
    @FXML
    private MenuItem undoMenuItem;
    @FXML
    private MenuItem redoMenuItem;

    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private Project selectedProject;
    private final UndoManager undoManager = new UndoManager();
    private final StatusBar statusBar = new StatusBar();

    final private String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    final private String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        this.setQuitMenuItem();
        this.setListToggleCheckMenuItem();
        this.setLayoutProperties();
        this.setNewProjectMenuItem();
        this.setProjectDetailsMenuItem();
        this.setUndoHandlers();

        this.setMainListView();
        this.setOpenMenu();
        this.setSaveMenu();
        this.setShortcuts();

        this.setStatusBar();
    }

    /**
     * Set save prompt when to handle request to close application
     */
     public void setClosePrompt() {
        primaryStage.setOnCloseRequest(event -> {
            if (undoManager.canRedoProperty.get()) {
                Action response = Dialogs.create()
                    .owner(primaryStage)
                    .title("Save Project")
                    .masthead("The project can be saved before exiting.")
                    .message("Would you like to save the project?")
                    .showConfirm();
                if (response == Dialog.ACTION_YES) {
                    try {
                        PersistenceManager.saveProject(this.selectedProject.getSaveLocation(),
                                                       this.selectedProject);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (response == Dialog.ACTION_CANCEL) {
                        event.consume();
                }
            }
        });
     }

    /**
     * Adds the status bar at the bottom of the application
     */
    private void setStatusBar() {
        this.mainBorderPane.setBottom(this.statusBar);
        this.statusBar.setText(this.ALL_CHANGES_SAVED_TEXT);
    }

    /**
     * Set the undo/redo item handlers, and also set their state depending on the undoManager.
     */
    private void setUndoHandlers() {
        this.undoMenuItem.setOnAction(event -> {
            this.undoManager.undoCommand();
            // Update status bar to show that there are unsaved changes.
            this.statusBar.setText(UNSAVED_CHANGES_TEXT);
        });

        this.redoMenuItem.setOnAction(event -> this.undoManager.redoCommand());

        this.undoManager.canUndoProperty.addListener((observable, oldValue, newValue) -> {
            this.undoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Undo 'Create Project')
                this.undoMenuItem.setText("Undo " + this.undoManager.getUndoType());
            } else {
                this.undoMenuItem.setText("Undo");
            }
        });

        this.undoManager.canRedoProperty.addListener((observable, oldValue, newValue) -> {
            this.redoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Redo 'Create Project');
                this.redoMenuItem.setText("Redo " + this.undoManager.getRedoType());
            } else {
                this.redoMenuItem.setText("Redo");
                if (this.undoManager.canUndoProperty.get()) {
                    this.undoMenuItem.setText("Undo " + this.undoManager.getUndoType());
                }
                this.statusBar.setText(this.ALL_CHANGES_SAVED_TEXT);
            }
        });

        this.undoManager.shouldUpdateMenuProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (this.undoManager.canUndoProperty.get()) {
                    this.undoMenuItem.setText("Undo " + this.undoManager.getUndoType());
                }
                if (this.undoManager.canRedoProperty.get()) {
                    this.redoMenuItem.setText("Redo " + this.undoManager.getRedoType());
                }
                this.undoManager.shouldUpdateMenuProperty.set(false);
            }
        });
    }

    private void setProjectDetailsMenuItem() {
        this.projectDetailsMenuItem.setOnAction(event -> {
            if (this.selectedProject != null) {
                this.editProjectDialog(this.selectedProject);
            } else {
                // Something went wrong and the button wasn't disabled, alert
                // the user
                // TODO
            }
        });
    }

    /**
     * Forces a redraw of the list view
     */
    private void refreshList() {
        final Project tmp = this.selectedProject;
        this.mainListView.setItems(null);
        this.mainListView.setItems(this.projects);
        this.mainListView.getSelectionModel().select(null);
        this.mainListView.getSelectionModel().select(tmp);
    }

    private void setSaveMenu() {
        this.saveMenuItem.setOnAction(event -> {
            try {
                PersistenceManager.saveProject(this.selectedProject.getSaveLocation(), this.selectedProject);
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            this.statusBar.setText(this.ALL_CHANGES_SAVED_TEXT);
        });
    }

    /**
     * Sets the shortcuts for the main window
     */
    private void setShortcuts() {
        this.newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        this.saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        this.openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        this.listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));

        // Undo/redo
        this.undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        this.redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
    }

    /**
     * Sets the handler so an open dialog is presented when the user clicks
     * File->Open
     */
    private void setOpenMenu() {
        this.openMenuItem.setOnAction(event -> {
            if (selectedProject != null) {
                Dialogs.create()
                        .owner(primaryStage)
                        .title("Error")
                        .message("Currently, only one project at a time is supported in this version.")
                        .showWarning();
                return;
            }


                final FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
                final File filePath = fileChooser.showOpenDialog(this.primaryStage);

                // Don't do anything if they press cancel/X
                if (filePath == null) {
                    return;
                }

                // Attempt to open the selected project
                try {
                    final Project project = PersistenceManager.loadProject(filePath);
                    addProject(project);
                    System.out.println(project.toString() + " has been loaded successfully");
                } catch (Exception e) {

                    Dialogs.create()
                            .owner(primaryStage)
                            .title("Couldn't load project")
                            .message("Your project could not be loaded. It may be corrupt or in an outdated format.")
                            .showError();
                }
            });
    }

    /**
     * Sets the content for the main list view
     */
    private void setMainListView() {
        this.mainListView.setItems(this.projects);

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Project");
        contextMenu.getItems().add(editContextMenu);

        this.mainListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> {
            if (this.selectedProject != null) {
                this.editProjectDialog(this.selectedProject);
            }
        });

        // Set change listener for mainListView
        this.mainListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedProject = newValue;
            if (newValue != null) {
                // Then a project is selected, enable the Project Details, and saveMenuItem
                this.projectDetailsMenuItem.setDisable(false);
                this.saveMenuItem.setDisable(false);
                this.newProjectMenuItem.setDisable(true);
            } else {
                // No project selected, disable Project Details MenuItem, and saveMenuItem
                this.projectDetailsMenuItem.setDisable(true);
                this.saveMenuItem.setDisable(true);
                this.newProjectMenuItem.setDisable(false);
            }
        });
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        this.listAnchorPane = (AnchorPane) this.mainSplitPane.getItems().get(0);

    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Sets the functionality for the quit menu item
     */
    private void setQuitMenuItem() {
        this.quitMenuItem.setOnAction(event -> {
            primaryStage.fireEvent(new WindowEvent(
                primaryStage,
                WindowEvent.WINDOW_CLOSE_REQUEST
            ));
        });
    }

    /**
     * Sets the functionality for the toggle list view menu item
     */
    private void setListToggleCheckMenuItem() {
        this.listToggleCheckMenuItem.setSelected(true);
        this.listToggleCheckMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // shows the list view
                this.mainSplitPane.getItems().add(0, this.listAnchorPane);
                this.mainSplitPane.setDividerPosition(0, this.dividerPosition);
            } else {
                // hides the list view
                this.dividerPosition = this.mainSplitPane.getDividerPositions()[0];
                this.mainSplitPane.getItems().remove(this.listAnchorPane);
            }
        });
    }

    private void setNewProjectMenuItem() {
        this.newProjectMenuItem.setOnAction(event -> this.newProjectDialog());
    }

    private void editProjectDialog(final Project project) {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("Edit Project");
            stage.initOwner(this.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/editProject.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final EditProjectController editProjectController = loader.getController();
            editProjectController.setStage(stage);
            editProjectController.loadProject(project);

            stage.showAndWait();
            if (editProjectController.isValid()) {
                Command c = new Command() {
                    CompoundCommand cc = editProjectController.getCommand();

                    @Override
                    public Object execute() {
                        // Add to mainListView
                        cc.execute();
                        refreshList();
                        return null;
                    }

                    @Override
                    public void undo() {
                        // Remove from mainListView
                        cc.undo();
                        refreshList();
                    }

                    @Override
                    public String getType() {
                        return "Edit Project";
                    }
                };
                undoManager.doCommand(c);
                refreshList();
            }
        });
    }

    private void newProjectDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initOwner(this.primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newProject.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final NewProjectController newProjectController = loader.getController();
            newProjectController.setStage(stage);

            stage.showAndWait();
            if (newProjectController.isValid()) {
                final Command<Project> c = new Command<Project>() {
                    private final CreateProjectCommand cpc = newProjectController.getCommand();

                    @Override
                    public Project execute() {
                        // Add to mainListView
                        final Project project = this.cpc.execute();
                        MainController.this.addProject(project);
                        return project;
                    }

                    @Override
                    public void undo() {
                        // Remove from mainListView
                        MainController.this.projects.remove(this.cpc.getProject());
                        this.cpc.undo();
                    }

                    @Override
                    public String getType() {
                        return this.cpc.getType();
                    }
                };

                //this.undoManager.doCommand(c);
                // We don't do the command, since it is not meant to be undoable at this stage
                c.execute();
                this.refreshList();
            }
        });
    }

    /**
     * Adds the new project to the observable list so that it is visible in the
     * list view
     *
     * @param project
     *            New Project to be added
     */
    private void addProject(final Project project) {
        if (project != null) {
            // Update View Accordingly
            this.projects.add(project);
            // Select added project in the ListView
            mainListView.getSelectionModel().select(project);
            // Attempt to write file to disk
            try {
                PersistenceManager.saveProject(project.getSaveLocation(), project);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
    }
}