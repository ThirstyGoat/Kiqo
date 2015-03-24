package seng302.group4.viewModel;

import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.util.Callback;
import org.controlsfx.control.StatusBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;
import seng302.group4.PersistenceManager;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;
import seng302.group4.undo.*;

import java.io.File;
import java.io.FileNotFoundException;
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
    private BorderPane mainBorderPane;
    @FXML
    private CheckMenuItem listToggleCheckMenuItem;
    @FXML
    private MenuItem quitMenuItem;
    @FXML
    private ListView<Project> mainListView;
    @FXML
    private ListView<Person> peopleListView;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private MenuItem newProjectMenuItem;
    @FXML
    private MenuItem newPersonMenuItem;
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
    @FXML
    private MenuItem editProjectMenuItem;
    @FXML
    private MenuItem editPersonMenuItem;
    @FXML
    private CheckMenuItem listShowProjectMenuItem;
    @FXML
    private CheckMenuItem listShowPeopleMenuItem;
    @FXML
    private Label listLabel;

    private Project selectedProject;
    private final UndoManager undoManager = new UndoManager();
    private Person selectedPerson;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private ObservableList<Person> people = FXCollections.observableArrayList();

    private final StatusBar statusBar = new StatusBar();
    final private String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    final private String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";

    private final SimpleBooleanProperty changesSaved = new SimpleBooleanProperty(true);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setQuitMenuItem();
        setListToggleCheckMenuItem();
        setLayoutProperties();
        setNewProjectMenuItem();
        setProjectDetailsMenuItem();
        setEditPersonMenuItem();
        setUndoHandlers();
        setNewPersonMenuItem();
        setMainListView();
        setPeopleListView();
        setOpenMenu();
        setSaveMenu();
        setShortcuts();
        showProjectListView();
        showPeopleListView();
        setStatusBar();
    }

    private void setEditPersonMenuItem() {
        editPersonMenuItem.setOnAction(event -> {
            if (selectedPerson != null) {
                editPersonDialog(selectedPerson);
            }
        });
    }

    /**
     * Shows the Project list view and hides the people list view
     */
    private void showProjectListView() {
        listShowProjectMenuItem.setSelected(true);
        listShowProjectMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                listLabel.setText("Project");
                listShowPeopleMenuItem.setSelected(false);
                selectedPerson = null;
                editPersonMenuItem.setDisable(true);

                peopleListView.setVisible(false);
                peopleListView.setManaged(false);

                mainListView.setVisible(true);
                mainListView.setManaged(true);
            } else {
                listShowPeopleMenuItem.setSelected(true);
            }
        });
    }

    /**
     * Shows the people list view and hides the project list view
     */
    private void showPeopleListView() {
        peopleListView.setManaged(false);
        listShowPeopleMenuItem.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && selectedProject != null) {
                listLabel.setText("People");
                listShowProjectMenuItem.setSelected(false);

                peopleListView.setVisible(true);
                peopleListView.setManaged(true);

                mainListView.setVisible(false);
                mainListView.setManaged(false);

                if (selectedProject != null) {
                    people.setAll(selectedProject.getPeople());
                }
                peopleListView.setItems(people);
            } else {
                listShowProjectMenuItem.setSelected(true);
            }
        });
    }

    /**
     * Set save prompt when to handle request to close application
     */
    public void setClosePrompt() {
        this.primaryStage.setOnCloseRequest(event -> {
            if (!this.changesSaved.get()) {
                final Action response = Dialogs.create().owner(this.primaryStage).title("Save Project")
                        .masthead("You have unsaved changes.").message("Would you like to save the changes you have made to the project?")
                        .showConfirm();
                if (response == Dialog.ACTION_YES) {
                    this.saveProject(this.selectedProject);
                } else if (response == Dialog.ACTION_CANCEL) {
                    event.consume();
                }
            }
        });
    }

    /**
     * Set up the status bar for the application and monitor for changes in the
     * save state
     */
    private void setStatusBar() {
        // Add the status bar to the bottom of the window
        this.mainBorderPane.setBottom(this.statusBar);

        // Set up listener for save status
        this.changesSaved.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If changes are saved, then update message to reflect that
                this.statusBar.setText(this.ALL_CHANGES_SAVED_TEXT);
            } else {
                // Then there are unsaved changes, update status message
                this.statusBar.setText(this.UNSAVED_CHANGES_TEXT);
            }
        });
    }

    /**
     * Set the undo/redo item handlers, and also set their state depending on
     * the undoManager.
     */
    private void setUndoHandlers() {
        this.undoMenuItem.setOnAction(event -> {
            this.undoManager.undoCommand();

            // If the changes are already saved, and we undo something, then the
            // changes are now not saved
            if (this.changesSaved.get()) {
                this.changesSaved.set(false);
            }
        });

        redoMenuItem.setOnAction(event -> {
            undoManager.redoCommand();
            // If changes are already saved, and we redo something, then changes
            // are now not saved
            if (this.changesSaved.get()) {
                this.changesSaved.set(false);
            }
        });

        this.undoManager.canUndoProperty.addListener((observable, oldValue, newValue) -> {
            this.undoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Undo 'Create Project')
                undoMenuItem.setText("Undo " + undoManager.getUndoType());
            } else {
                undoMenuItem.setText("Undo");
            }
        });

        undoManager.canRedoProperty.addListener((observable, oldValue, newValue) -> {
            redoMenuItem.setDisable(!newValue);
            if (newValue) {
                // Update text to say (eg. Redo 'Create Project');
                redoMenuItem.setText("Redo " + undoManager.getRedoType());
            } else {
                redoMenuItem.setText("Redo");
                if (undoManager.canUndoProperty.get()) {
                    undoMenuItem.setText("Undo " + undoManager.getUndoType());
                }
            }
        });

        undoManager.shouldUpdateMenuProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (undoManager.canUndoProperty.get()) {
                    undoMenuItem.setText("Undo " + undoManager.getUndoType());
                }
                if (undoManager.canRedoProperty.get()) {
                    redoMenuItem.setText("Redo " + undoManager.getRedoType());
                }
                undoManager.shouldUpdateMenuProperty.set(false);
            }
        });
    }

    private void setProjectDetailsMenuItem() {
        this.projectDetailsMenuItem.setOnAction(event -> {
            if (this.selectedProject != null) {
                this.editProjectDialog(this.selectedProject);
            }
        });
    }

    /**
     * Forces a redraw of the list view
     */
    private void refreshList() {
        Project tmp = selectedProject;
        Person tempPerson = selectedPerson;
        mainListView.setItems(null);
        mainListView.setItems(projects);
        mainListView.getSelectionModel().select(null);
        mainListView.getSelectionModel().select(tmp);

        peopleListView.setItems(null);
        peopleListView.setItems(people);
        peopleListView.getSelectionModel().select(null);
        peopleListView.getSelectionModel().select(tempPerson);
    }

    private void setSaveMenu() {
        this.saveMenuItem.setOnAction(event -> {
            this.saveProject(this.selectedProject);
        });
    }

    /**
     * Sets the shortcuts for the main window
     */
    private void setShortcuts() {
        newProjectMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));
        newPersonMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));
        listToggleCheckMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN));

        // Undo/redo
        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN));
        redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN,
                KeyCombination.SHIFT_DOWN));
    }

    /**
     * Sets the handler so an open dialog is presented when the user clicks
     * File->Open
     */
    private void setOpenMenu() {
        this.openMenuItem.setOnAction(event -> {
            if (this.selectedProject != null) {
                Dialogs.create().owner(this.primaryStage).title("Error")
                        .message("Currently, only one project at a time is supported in this version.").showWarning();
                return;
            }

            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
            final File filePath = fileChooser.showOpenDialog(this.primaryStage);

            if (filePath == null) {
                return;
            }
            // TODO Actually do something with the selected file
            Project project = null;
            try {
                project = PersistenceManager.loadProject(filePath);
            } catch (JsonSyntaxException | InvalidProjectException e) {
                System.out.println("JSON file invalid");
                e.printStackTrace();
            } catch (InvalidPersonException e) {
                System.out.println("Person invalid");
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                e.printStackTrace();
            }
            if (project != null) {
                project.setSaveLocation(filePath);
                projects.add(project);
                System.out.println(project.toString() + " has been loaded successfully");
            }
        });
    }

    /**
     * Sets the content for the main list view
     */
    private void setMainListView() {
        // derived from example at
        // http://docs.oracle.com/javafx/2/api/javafx/scene/control/Cell.html
        this.mainListView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(final ListView<Project> arg0) {
                return new ListCell<Project>() {
                    @Override
                    protected void updateItem(final Project project, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(project, empty);
                        this.setText(empty ? "" : project.getShortName());
                    }
                };
            }
        });
        this.mainListView.setItems(this.projects);

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
        this.mainListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            this.selectedProject = newValue;

            // Update status bar to show current save status of selected project
            // Probably not the best way to do this, but it's the simplest
            changesSaved.set(!changesSaved.get());
            changesSaved.set(!changesSaved.get());

            if (newValue != null) {
                newPersonMenuItem.setDisable(false);
                // Then a project is selected, enable the Project Details, and
                // saveMenuItem
                this.projectDetailsMenuItem.setDisable(false);
                this.saveMenuItem.setDisable(false);
            } else {
                newPersonMenuItem.setDisable(true);
                // No project selected, disable Project Details MenuItem, and
                // saveMenuItem
                this.projectDetailsMenuItem.setDisable(true);
                this.saveMenuItem.setDisable(true);
                // Then a project is selected, enable the Project Details
                // MenuItem
                this.projectDetailsMenuItem.setDisable(false);
            }
        });
    }

    /**
     * Sets the content for the main list view
     */
    private void setPeopleListView() {
        this.peopleListView.setCellFactory(new Callback<ListView<Person>, ListCell<Person>>() {
            @Override
            public ListCell<Person> call(final ListView<Person> arg0) {
                return new ListCell<Person>() {
                    @Override
                    protected void updateItem(final Person person, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(person, empty);
                        this.setText(empty ? "" : person.getShortName());
                    }
                };
            }
        });
        this.peopleListView.setItems(this.people);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editContextMenu = new MenuItem("Edit Person");
        contextMenu.getItems().add(editContextMenu);

        peopleListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> {
            if (selectedProject != null) {
                editPersonDialog(selectedPerson);
            }
        });

        // Set change listener for mainListView
        peopleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                editPersonMenuItem.setDisable(false);
                selectedPerson = newValue;
            }
        });
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        listAnchorPane = (AnchorPane) mainSplitPane.getItems().get(0);

    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Sets the functionality for the quit menu item
     */
    private void setQuitMenuItem() {
        // We could just call primaryStage.close(), but that is a force close,
        // and then we can't prompt for saving changes
        this.quitMenuItem.setOnAction(event -> this.primaryStage.fireEvent(new WindowEvent(this.primaryStage,
                WindowEvent.WINDOW_CLOSE_REQUEST)));
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
            if (this.selectedProject != null) {
                Dialogs.create().owner(this.primaryStage).title("Error")
                        .message("Currently, only one project at a time is supported in this version.").showWarning();
                return;
            } else {
                newProjectDialog();
            }


        });
    }

    private void setNewPersonMenuItem() {
        newPersonMenuItem.setOnAction(event -> {
            if (selectedProject != null) {
                newPersonDialog();
            }
        });
    }

    private void editProjectDialog(Project project) {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("Edit Project");
            stage.initOwner(primaryStage);
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
                final Command c = new Command() {
                    CompoundCommand cc = editProjectController.getCommand();

                    @Override
                    public Void execute() {
                        // Add to mainListView
                        this.cc.execute();
                        MainController.this.saveProject(project);
                        MainController.this.refreshList();
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

    /**
     * Saves the project to disk and marks project as saved.
     *
     * @param project
     *            Project to be saved.
     */
    private void saveProject(final Project project) {
        try {
            PersistenceManager.saveProject(project.getSaveLocation(), project);
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }
        this.changesSaved.set(true);
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
                        Project project = cpc.execute();
                        addProject(project);
                        return project;
                    }

                    @Override
                    public void undo() {
                        // Remove from mainListView
                        projects.remove(cpc.getProject());
                        cpc.undo();
                    }

                    @Override
                    public String getType() {
                        return cpc.getType();
                    }
                };

                // this.undoManager.doCommand(c);
                // We don't do the command, since it is not meant to be undoable
                // at this stage
                c.execute();
                this.refreshList();
            }
        });
    }

    private void editPersonDialog(Person person) {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle("Edit Project");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/editPerson.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            EditPersonController editPersonController = loader.getController();
            editPersonController.setStage(stage);
            editPersonController.setProject(selectedProject);
            editPersonController.loadPerson(person);



            stage.showAndWait();

            if (editPersonController.isValid()) {
                Command c = new Command() {
                    CompoundCommand cc = editPersonController.getCommand();

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
                        return "Edit Person";
                    }
                };
                undoManager.doCommand(c);
                refreshList();
            }

            refreshList();
        });
    }

    private void newPersonDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newPerson.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene scene = new Scene(root);
            stage.setScene(scene);
            NewPersonController newPersonController = loader.getController();
            newPersonController.setStage(stage);
            newPersonController.setProject(selectedProject);


            stage.showAndWait();
            if (newPersonController.isValid()) {
                Command c = new Command() {
                    CreatePersonCommand cpc = newPersonController.getCommand();

                    @Override
                    public Object execute() {
                        // Add to mainListView
                        Person person = cpc.execute();
                        selectedProject.addPerson(person);
                        addPersonToList(person);
                        return person;
                    }

                    @Override
                    public void undo() {
                        // Remove from mainListView
                        people.remove(cpc.getPerson());
                        selectedProject.removePerson(cpc.getPerson());
                        cpc.undo();
                    }

                    @Override
                    public String getType() {
                        return cpc.getType();
                    }
                };

                undoManager.doCommand(c);
            }
            refreshList();
        });
    }

    private void addPersonToList(Person person) {
        if (person != null) {
            // Update view accordingly
            people.add(person);
            // Select added person in the listView
            peopleListView.getSelectionModel().select(person);
            // Save the project
            saveProject(selectedProject);
        }
    }


    /**
     * Adds the new project to the observable list so that it is visible in the list view
     * @param project New Project to be added
     */
    private void addProject(final Project project) {
        if (project != null) {
            // Update View Accordingly
            projects.add(project);
            // Select added project in the ListView
            mainListView.getSelectionModel().select(project);
            // Save the project
            saveProject(project);
        }
    }
}
