package com.thirstygoat.kiqo.viewModel;

import com.google.gson.JsonSyntaxException;
import com.thirstygoat.kiqo.PersistenceManager;
import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.exceptions.InvalidPersonException;
import com.thirstygoat.kiqo.exceptions.InvalidProjectException;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.nodes.GoatDialog;
import com.thirstygoat.kiqo.reportGenerator.ReportGenerator;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.detailControllers.MainDetailsPaneController;
import com.thirstygoat.kiqo.viewModel.formControllers.AllocationFormController;
import com.thirstygoat.kiqo.viewModel.formControllers.IFormController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Callback;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {

    private static final String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    private static final String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";
    private static final String PRODUCT_NAME = "Kiqo";
    public final ObjectProperty<Item> focusedItemProperty = new SimpleObjectProperty<>();
    public final ObjectProperty<Organisation> selectedOrganisationProperty = new SimpleObjectProperty<>();
    private final UndoManager undoManager = new UndoManager();
    private final SimpleBooleanProperty changesSaved = new SimpleBooleanProperty(true);
    // BEGIN FXML INJECTIONS
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private Pane listPane;
    @FXML
    private Pane detailsPane;
    @FXML
    private MainDetailsPaneController detailsPaneController;
    @FXML
    private TabPane sideBar;
    @FXML
    private SideBarController sideBarController;
    @FXML
    private MenuBarController menuBarController;
    private Stage primaryStage;
    private double dividerPosition;
    private int savePosition = 0;

    private void setStageTitleProperty() {
        // Add a listener to know when changes are saved, so that the title can be updated
        final StringProperty changesSavedAsterisk = new SimpleStringProperty(changesSaved.get() ? "" : "*");
        changesSaved.addListener((observable, oldValue, newValue) -> {
            changesSavedAsterisk.set(newValue ? "" : "*");
        });

        final StringProperty orgName = new SimpleStringProperty();
        orgName.bind(selectedOrganisationProperty.get().organisationNameProperty());
        selectedOrganisationProperty.addListener((observable, oldValue, newValue) -> {
            orgName.unbind();
            orgName.bind(newValue.organisationNameProperty());
        });

        primaryStage.titleProperty().bind(Bindings
                .concat(orgName)
                .concat(changesSavedAsterisk)
                .concat(" - ")
                .concat(MainController.PRODUCT_NAME));
    }

    /**
     * @param project organisation to be deleted
     *
     */
    private void deleteProject(Project project) {
        final DeleteProjectCommand command = new DeleteProjectCommand(project, selectedOrganisationProperty.get());

        final String[] buttons = {"Delete Project", "Cancel"};
        final String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Project", "Are you sure?",
                "Are you sure you want to delete the project " + project.getShortName() + "?", buttons);

        if (result.equals("Delete Project")) {
            doCommand(command);
        }
    }

    private void deleteSkill(Skill skill) {
        if (skill == selectedOrganisationProperty.get().getPoSkill() || skill == selectedOrganisationProperty.get().getSmSkill()) {
            GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                    "The Product Owner and Scrum Master skills cannot be deleted.");
        } else {

            String deleteMessage = "There are no people with this skill.";
            final DeleteSkillCommand command = new DeleteSkillCommand(skill, selectedOrganisationProperty.get());
                if (command.getPeopleWithSkill().size() > 0) {
                deleteMessage = "Deleting the skill will also remove it from the following people:\n";
                deleteMessage += Utilities.concatenatePeopleList((command.getPeopleWithSkill()), 5);
            }
            final String[] buttons = { "Delete Skill", "Cancel" };
            final String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Skill",
                    "Are you sure you want to delete the skill " + skill.getShortName() + "?", deleteMessage, buttons);

            if (result.equals("Delete Skill")) {
                doCommand(new DeleteSkillCommand(skill, selectedOrganisationProperty.get()));
            }
        }
    }

    private void deleteTeam(Team team) {
        final VBox node = new VBox();
        node.setSpacing(10);

        CheckBox checkbox;

        if (team.getTeamMembers().size() > 0) {
            checkbox = new CheckBox("Also delete the people belonging to this team");
            String deleteMessage = "Are you sure you want to delete the team: " + team.getShortName() +
                    "?\nCurrent team members:\n";
            deleteMessage += Utilities.concatenatePeopleList(team.getTeamMembers(), 5);
            node.getChildren().add(new Label(deleteMessage));
            node.getChildren().add(checkbox);
        } else {
            final String deleteMessage = "Are you sure you want to delete the team: " + team.getShortName() +
                    "?\nThis team has nobody in it.";
            node.getChildren().add(new Label(deleteMessage));
            checkbox = null;
        }

        final String[] buttons = { "Delete Team", "Cancel" };
        final String result = GoatDialog.createCustomNodeDialog(primaryStage, "Delete Team", "Are you sure?", node, buttons);

        // change this because its hasn't been init yet
        final boolean deletePeople = (checkbox != null) ? checkbox.selectedProperty().getValue() : false;

        if (result.equals("Delete Team")) {
            // Then delete the team
            // The result of whether or not to delete the team members can be
            // fetched by deletePeople boolean
            final DeleteTeamCommand command = new DeleteTeamCommand(team, selectedOrganisationProperty.get());
            if (deletePeople) {
                command.setDeleteMembers();
            }
            doCommand(command);
        }
    }

    private void deletePerson(Person person) {

        final VBox node = new VBox();
        node.setSpacing(10);

        String deleteMessage = "Are you sure you want to remove the person: " + person.getShortName() + "?";
        if (person.getTeam() != null) {
            deleteMessage += "\nThis will remove " + person.getShortName() + " from team ";
            deleteMessage += person.getTeam().getShortName() + ".";
        }
        node.getChildren().add(new Label(deleteMessage));

        final String[] buttons = {"Delete Person", "Cancel"};

        final String result = GoatDialog.createCustomNodeDialog(primaryStage, "Delete Person",
                "Are you sure? ", node, buttons);

        if (result.equals("Delete Person")) {
            doCommand(new DeletePersonCommand((Person) focusedItemProperty.get(), selectedOrganisationProperty.get()));
        }
    }

    public void deleteRelease(Release release) {
       final VBox node = new VBox();
        node.setSpacing(10);

        final String deleteMessage = "Are you sure you want to remove the release: "
                + release.getShortName() + ", " + release.getDate() + "?";
        node.getChildren().add(new Label(deleteMessage));

        final String[] buttons = {"Delete Release", "Cancel"};
        final String result = GoatDialog.createCustomNodeDialog(primaryStage, "Delete Release",
                "Are you sure? ", node, buttons);

        if (result.equals("Delete Release")) {
            doCommand(new DeleteReleaseCommand((Release) focusedItemProperty.get()));
        }

    }

    public void deleteItem() {
        Platform.runLater(() -> {
            final Item focusedObject = focusedItemProperty.get();
            if (focusedObject == null) {
                // do nothing
            } else if (focusedObject instanceof Project) {
                deleteProject((Project) focusedObject);
            } else if (focusedObject instanceof Person) {
                deletePerson((Person) focusedObject);
            } else if (focusedObject instanceof Skill) {
                deleteSkill((Skill) focusedObject);
            } else if (focusedObject instanceof Team) {
                deleteTeam((Team) focusedObject);
            } else if (focusedObject instanceof Release) {
                deleteRelease((Release) focusedObject);
            }
        });
    }

    public void editItem() {
        final Item focusedObject = focusedItemProperty.get();
        if (focusedObject == null) {
            // do nothing
        } else if (focusedObject instanceof Project) {
            dialog((Project) focusedObject);
        } else if (focusedObject instanceof Person) {
            dialog((Person) focusedObject);
        } else if (focusedObject instanceof Skill) {
            if (focusedObject == selectedOrganisationProperty.get().getPoSkill() || focusedObject == selectedOrganisationProperty.get().getSmSkill()) {
                GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                        "The Product Owner and Scrum Master skills cannot be edited.");
            } else {
                dialog((Skill) focusedObject);
            }
        } else if (focusedObject instanceof Team) {
            dialog((Team) focusedObject);
        } else if (focusedObject instanceof Release) {
            dialog((Release) focusedObject);
        }
    }

    /**
     * Exits the application after prompting to save unsaved changes.
     *
     * We could just call primaryStage.close(), but that is a force close, and
     * then we can't prompt for saving changes
     */
    public void exit() {
        primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedOrganisationProperty.set(new Organisation());

        // enable menu items
        menuBarController.enableNewTeam();
        menuBarController.enableNewPerson();
        menuBarController.enableNewSkill();

        saveStateChanges();
        menuBarController.setListenersOnUndoManager(undoManager);
        focusedItemProperty.addListener((observable, oldValue, newValue) -> {
            System.out.println("Focus changed to " + newValue);
            detailsPaneController.showDetailsPane(newValue);
            menuBarController.updateAfterAnyObjectSelected(newValue != null);
        });

        selectedOrganisationProperty.addListener((observable, oldValue, newValue) -> {
            // Clear undo/redo stack
            undoManager.empty();
        });
    }

    public ObjectProperty<Organisation> getSelectedOrganisationProperty() {
        return selectedOrganisationProperty;
    }

    public void newSkill() {
        if (selectedOrganisationProperty.get() != null) {
            dialog(null, "Skill");
        }
    }

    public void newPerson() {
        if (selectedOrganisationProperty.get() != null) {
            dialog(null, "Person");
        }
    }

    public void newTeam() {
        if (selectedOrganisationProperty.get() != null) {
            dialog(null, "Team");
        }
    }

    public void newRelease() {
        if (selectedOrganisationProperty.get() != null) {
            dialog(null, "Release");
        }
    }

    public void newProject() {
        if (selectedOrganisationProperty.get() != null) {
            dialog(null, "Project");
        }
    }

    public void allocateTeams() {
        if (selectedOrganisationProperty.get() != null ) {
            allocationDialog(null);
        }
    }

    public void openOrganisation(File draggedFilePath) {
        File filePath;

        if (selectedOrganisationProperty.get() != null) {
            if(!promptForUnsavedChanges()) {
                return;
            }
        }

        if (draggedFilePath == null) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
            filePath = fileChooser.showOpenDialog(primaryStage);
        } else {
            filePath = draggedFilePath;
        }

        if (filePath == null) {
            return;
        }
        Organisation organisation = null;
        try {
            organisation = PersistenceManager.loadOrganisation(filePath);
            selectedOrganisationProperty.set(organisation);
            changesSaved.set(true);
        } catch (JsonSyntaxException | InvalidProjectException e) {
            GoatDialog.showAlertDialog(primaryStage, "Error Loading Project", "No can do.", "The JSON file you supplied is invalid.");
        } catch (final InvalidPersonException e) {
            GoatDialog.showAlertDialog(primaryStage, "Person Invalid", "No can do.", "An invalid person was found.");
        } catch (final FileNotFoundException e) {
            GoatDialog.showAlertDialog(primaryStage, "File Not Found", "No can do.", "Somehow, the file you tried to open was not found.");
        }

        if(PersistenceManager.getIsOldJSON()) {
            GoatDialog.showAlertDialog(primaryStage, "Warning", "An old JSON file has been loaded.", "You will need to allocate teams to your project [Project > Allocate Teams].");
            PersistenceManager.resetIsOldJSON();
        }
    }

    /**
     * Saves the project to disk and marks project as saved.
     */
    public void saveOrganisation() {
        final Organisation organisation = selectedOrganisationProperty.get();
        // ask for save location if not yet set
        if (organisation.getSaveLocation() == null) {
            final FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
            final File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                organisation.setSaveLocation(file);
            }
        }
        // if successfully set
        if (organisation.getSaveLocation() != null) {
            try {
                PersistenceManager.saveOrganisation(organisation.getSaveLocation(), organisation);
            } catch (final IOException e) {
                GoatDialog.showAlertDialog(primaryStage, "Save failed", "No can do.", "Somehow, that file didn't allow saving.");
                return;
            }
            savePosition = undoManager.getUndoStackSize();
            changesSaved.set(true);
        }
    }

    /**
     * Prompts the user for a new save location via a filechooser.
     * Updates the organisation's current save location.
     * Saves the current organisation to it.
     */
    public void saveAsOrganisation() {
        final Organisation organisation = selectedOrganisationProperty.get();
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
        final File existingFile = selectedOrganisationProperty.get().getSaveLocation();
        if (existingFile != null) {
            fileChooser.setInitialDirectory(existingFile.getParentFile());
            fileChooser.setInitialFileName(existingFile.getName());
        }
        final File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            organisation.setSaveLocation(file);
        }
        if (organisation.getSaveLocation() != null) {
            try {
                PersistenceManager.saveOrganisation(organisation.getSaveLocation(), organisation);
            } catch (final IOException e) {
                GoatDialog.showAlertDialog(primaryStage, "Save failed", "No can do.", "Somehow, that file didn't allow saving.");
                return;
            }
            changesSaved.set(true);
        }
    }

    public void setListVisible(boolean visible) {
        if (visible) {
            // shows the list view
            mainSplitPane.getItems().add(0, listPane);
            mainSplitPane.setDividerPosition(0, dividerPosition);
        } else {
            // hides the list view
            dividerPosition = mainSplitPane.getDividerPositions()[0];
            mainSplitPane.getItems().remove(listPane);
        }
    }

    public void saveStatusReport() {
        final String EXTENSION = ".yaml";
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("yaml Files", "*" + EXTENSION));
        final File existingFile = selectedOrganisationProperty.get().getSaveLocation();
        if (existingFile != null) {
            fileChooser.setInitialDirectory(existingFile.getParentFile());
            fileChooser.setInitialFileName(selectedOrganisationProperty.get().organisationNameProperty().get());
        }

        final File selectedFile = fileChooser.showSaveDialog(primaryStage);

        if (selectedFile != null) {
            try (final FileWriter fileWriter = new FileWriter(selectedFile)) {
                final ReportGenerator reportGenerator = new ReportGenerator(selectedOrganisationProperty.get());
                fileWriter.write(reportGenerator.generateReport());
                fileWriter.close();
            } catch(final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void undo() {
        undoManager.undoCommand();
        // If the changes are already saved, and we undo something, then the changes are now not saved
        changesSaved.set(undoManager.getUndoStackSize() == savePosition);
    }

    public void redo() {
        undoManager.redoCommand();
        // If the changes are already saved, and we redo something, then the changes are now not saved

        changesSaved.set(undoManager.getUndoStackSize() == savePosition);
    }

    public void doCommand(Command<?> command) {
        undoManager.doCommand(command);
        changesSaved.set(false);
    }

    /**
     * Set save prompt when to handle request to close application
     */
    private void addClosePrompt() {
        primaryStage.setOnCloseRequest(event -> {
            if (!promptForUnsavedChanges()) {
                event.consume();
            }
        });
    }

    /**
     * Prompt the user if they want to save unsaved changes
     * @return if the user clicked cancel or not
     */
    private boolean promptForUnsavedChanges() {
        if (!changesSaved.get()) {
            final String[] options = {"Save changes", "Discard changes", "Cancel"};
            final String response = GoatDialog.createBasicButtonDialog(primaryStage, "Save Project", "You have unsaved changes.",
                    "Would you like to save the changes you have made to the project?", options);
            if (response.equals("Save changes")) {
                saveOrganisation();
            } else if (response.equals("Cancel")) {
                // do nothing
                return false;
            }
        }
        return true;
    }

    /**
     * Set up the status bar for the application and monitor for changes in the
     * save state
     */
    private void saveStateChanges() {
        final StatusBar statusBar = new StatusBar();
        // Add the status bar to the bottom of the window
        mainBorderPane.setBottom(statusBar);

        // Set up listener for save status
        changesSaved.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If changes are saved, then update message to reflect that
                statusBar.setText(MainController.ALL_CHANGES_SAVED_TEXT);
            } else {
                // Then there are unsaved changes, update status message
                statusBar.setText(MainController.UNSAVED_CHANGES_TEXT);
            }
        });
    }

    /**
     * Attaches cell factory and selection listener to the list view.
     */
    private <T extends Item> void initialiseListView(ListView<T> listView, ContextMenu contextMenu) {
        // derived from example at
        // http://docs.oracle.com/javafx/2/api/javafx/scene/control/Cell.html
        listView.setCellFactory(new Callback<ListView<T>, ListCell<T>>() {
            @Override
            public ListCell<T> call(final ListView<T> arg0) {
                final ListCell<T> listCell = new ListCell<T>() {
                    @Override
                    protected void updateItem(final T item, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(item, empty);
                        setText(empty ? "" : item.getShortName());
                        if (item != null) {
                            item.shortNameProperty().addListener((observable, oldValue, newValue) -> {
                                setText(newValue);
                            });
                            setContextMenu(contextMenu);
                        }
                    }
                };
                return listCell;
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Update status bar to show current save status
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());
            }
        });
    }

    /**
     * Convenience method for {dialog(t, type)}
     * @param t must not be null
     */
    private <T> void dialog(T t) {
        final String[] fullname = t.getClass().getName().split("\\.");
        final String name = fullname[fullname.length - 1];
        dialog(t, name);
    }

    /**
     *
     * @param t may be null
     * @param type type of t. This is displayed in the dialog title and also used to retrieve the fxml file, eg. "Project" => "forms/project.fxml".
     */
    private <T> void dialog(T t, String type) {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.setTitle(t == null ? "New " : "Edit " + type);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("forms/" + type.toLowerCase() + ".fxml"));
            Pane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            @SuppressWarnings("unchecked")
            final IFormController<T> formController = (IFormController<T>) loader.getController();
            formController.setStage(stage);
            formController.setOrganisation(selectedOrganisationProperty.get());
            formController.populateFields(t);

            stage.showAndWait();
            if (formController.isValid()) {
                doCommand(formController.getCommand());
            }
        });
    }

    private void allocationDialog(Allocation allocation) {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("forms/allocation.fxml"));
            Pane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final AllocationFormController allocationFormController = loader.getController();
            allocationFormController.setStage(stage);
            allocationFormController.setOrganisation(selectedOrganisationProperty.get());

            if (focusedItemProperty.getValue().getClass().equals(Team.class)) {
                allocationFormController.setProject(null);
                allocationFormController.setTeam((Team) focusedItemProperty.getValue());
            } else if (focusedItemProperty.getValue().getClass().equals(Project.class)) {
                allocationFormController.setProject((Project) focusedItemProperty.getValue());
                allocationFormController.setTeam(null);
            }

            allocationFormController.populateFields(allocation);

            stage.showAndWait();
            if (allocationFormController.isValid()) {
                doCommand(allocationFormController.getCommand());
            }
        });
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
        addClosePrompt();
        menuBarController.setMainController(this);
        detailsPaneController.setMainController(this);
        sideBarController.setMainController(this);

        setStageTitleProperty();
    }

    public void newOrganisation() {
        if (selectedOrganisationProperty.get() != null) {
            if(!promptForUnsavedChanges()) {
                return;
            }
        }
        selectedOrganisationProperty.set(new Organisation());
    }

    public SideBarController getSideBarController() {
        return sideBarController;
    }
}