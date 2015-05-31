package com.thirstygoat.kiqo.viewModel;

import com.google.gson.JsonSyntaxException;
import com.thirstygoat.kiqo.Main;
import com.thirstygoat.kiqo.PersistenceManager;
import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.exceptions.InvalidPersonDeletionException;
import com.thirstygoat.kiqo.exceptions.InvalidPersonException;
import com.thirstygoat.kiqo.exceptions.InvalidProjectException;
import com.thirstygoat.kiqo.model.*;
import com.thirstygoat.kiqo.nodes.GoatDialog;
import com.thirstygoat.kiqo.reportGenerator.ReportGenerator;
import com.thirstygoat.kiqo.util.ApplicationInfo;
import com.thirstygoat.kiqo.util.Utilities;
import com.thirstygoat.kiqo.viewModel.detailControllers.MainDetailsPaneController;
import com.thirstygoat.kiqo.viewModel.formControllers.AllocationFormController;
import com.thirstygoat.kiqo.viewModel.formControllers.FormController;
import com.thirstygoat.kiqo.viewModel.formControllers.ReportFormController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import org.controlsfx.control.StatusBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static final String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    private static final String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";
    private static final String PRODUCT_NAME = ApplicationInfo.getProperty("name");
    public final ObjectProperty<Item> focusedItemProperty = new SimpleObjectProperty<>();
    public final SimpleObjectProperty<Organisation> selectedOrganisationProperty = new SimpleObjectProperty<>();
    private final UndoManager undoManager = new UndoManager();
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

    public ReadOnlyBooleanProperty changesSavedProperty() {
        return undoManager.changesSavedProperty();
    }

    protected void revert() {
//        undoManager.revert();

        File tempSaveLocation = selectedOrganisationProperty.get().getSaveLocation();
        // If there is no save location then it has never been saved before so we can just set it to a new Org
        if (tempSaveLocation != null) {
            selectedOrganisationProperty.set(PersistenceManager.revert());
            selectedOrganisationProperty.get().setSaveLocation(tempSaveLocation);
        } else {
            selectedOrganisationProperty.set(new Organisation());
        }
        // Empty the undo/redo stack(s)
        undoManager.empty();
        undoManager.changesSavedProperty().setValue(true);
    }

    /**
     * Sets the stage title and updates accordingly depending on the organisation
     * selected, and whether or not changes have been saved.
     */
    private void setStageTitleProperty() {
        // Add a listener to know when changes are saved, so that the title can be updated
        final StringProperty changesSavedAsterisk = new SimpleStringProperty(undoManager.changesSavedProperty().get() ? "" : "*");
        undoManager.changesSavedProperty().addListener((observable, oldValue, newValue) -> {
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
     * Prompts to delete a project
     * @param project Project to be deleted
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

    /**
     * Prompts to delete a story
     * @param story Story to be deleted
     */
    private void deleteStory(Story story) {
        final DeleteStoryCommand command = new DeleteStoryCommand(story);
        final String[] buttons = { "Delete Story", "Cancel" };
        final String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Story", "Are you sure?",
                "Are you sure you want to delete the story " + story.getShortName() + "?", buttons);

        if (result.equals("Delete Story")) {
            doCommand(command);
        }
    }

    /**
     * Prompts to delete a skill (Note that the PO & SM skills can not be deleted)
     * @param skill Skill to be deleted
     */
    private void deleteSkill(Skill skill) {
        if (skill == selectedOrganisationProperty.get().getPoSkill() || skill == selectedOrganisationProperty.get().getSmSkill()) {
            GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                    "The Product Owner and Scrum Master skills cannot be deleted.");
        } else {
            String deleteMessage = "There are no people with this skill.";
            final DeleteSkillCommand command = new DeleteSkillCommand(skill, selectedOrganisationProperty.get());
                if (command.getPeopleWithSkill().size() > 0) {
                deleteMessage = "Deleting the skill will also remove it from the following people:\n";
                deleteMessage += Utilities.concatenateItemsList((command.getPeopleWithSkill()), 5);
            }
            final String[] buttons = { "Delete Skill", "Cancel" };
            final String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Skill",
                    "Are you sure you want to delete the skill " + skill.getShortName() + "?", deleteMessage, buttons);

            if (result.equals("Delete Skill")) {
                doCommand(new DeleteSkillCommand(skill, selectedOrganisationProperty.get()));
            }
        }
    }

    /**
     * Prompts to delete a team
     * @param team Team to be deleted
     */
    private void deleteTeam(Team team) {
        final DeleteTeamCommand command = new DeleteTeamCommand(team, selectedOrganisationProperty().get());

        final VBox node = new VBox();
        node.setSpacing(10);

        CheckBox checkbox = new CheckBox("Also delete the person belonging to this team");

        if (team.getTeamMembers().size() > 0) {
            String deleteMessage = "Are you sure you want to delete the team: " + team.getShortName() +
                    "?\nMembers: " + Utilities.concatenateItemsList(team.getTeamMembers(), 5);
            node.getChildren().add(new Label(deleteMessage));
            if (command.canDeleteTeamMembers()) {
                node.getChildren().add(checkbox);
            } else {
                Label unableToDeletePeopleMessage = new Label("The members of this team can not be deleted as " +
                        "one or more of them is PO of a backlog.");
                unableToDeletePeopleMessage.setWrapText(true);
                unableToDeletePeopleMessage.setStyle("-fx-font-style: italic");
                node.getChildren().add(unableToDeletePeopleMessage);
            }
        } else {
            final String deleteMessage = "Are you sure you want to delete the team: " + team.getShortName() +
                    "?\nThis team has nobody in it.";
            node.getChildren().add(new Label(deleteMessage));
        }

        final String[] buttons = { "Delete Team", "Cancel" };
        final String result = GoatDialog.createCustomNodeDialog(primaryStage, "Delete Team", "Are you sure?", node, buttons);

        final boolean deletePeople = checkbox.selectedProperty().getValue();

        if (result.equals("Delete Team")) {
            // Then delete the team
            // The result of whether or not to delete the team members can be
            // fetched by deletePeople boolean
            if (deletePeople) {
                command.setDeleteTeamMembers(true);
            }
            doCommand(command);
        }
    }

    /**
     * Prompts to delete a person
     * @param person Person to be deleted
     */
    private void deletePerson(Person person) {
        DeletePersonCommand command;
        try {
            command = new DeletePersonCommand(person, selectedOrganisationProperty().get());
        } catch (InvalidPersonDeletionException e) {
            // Then this person can not be deleted since they are the owner of one or more backlogs
            // Let us find out which backlogs they are PO of and alert them to this

            final List<Backlog> backlogsOwned = new ArrayList<>();
            // Check if they are the PO of any backlogs
            for (Project project : selectedOrganisationProperty().get().getProjects()) {
                backlogsOwned.addAll(project.observableBacklogs().stream()
                        .filter(backlog -> backlog.getProductOwner() == person).collect(Collectors.toList()));
            }

            GoatDialog.showAlertDialog(
                    primaryStage,
                    "Can't delete Person",
                    "Can't delete Person",
                    person.getShortName() + " is currently the PO of the following " +
                            Utilities.pluralise(backlogsOwned.size(), "Backlog", "Backlogs") + ":\n" +
                            Utilities.concatenateItemsList(backlogsOwned, 5)
            );
            return;
        }

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
            doCommand(command);
        }
    }

    /**
     * Prompts to delete a release
     * @param release Release to be deleted
     */
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

    /**
     * Prompts to delete a backlog
     * @param backlog Backlog to be deleted
     */
    public void deleteBacklog(Backlog backlog) {
        final VBox node = new VBox();
        node.setSpacing(10);

        CheckBox checkBox;

        if (backlog.getStories().size() > 0) {
            checkBox = new CheckBox("Also delete the stories allocated to this backlog");
            String deleteMessage = "Are you sure you want to delete the backlog: " + backlog.getShortName() +
                    "?\nCurrent stories:\n";
            deleteMessage += Utilities.concatenateItemsList(backlog.getStories(), 5);
            node.getChildren().add(new Label(deleteMessage));
            node.getChildren().add(checkBox);
        } else {
            final String deleteMessage = "Are you sure you want to remove the backlog: "
                    + backlog.getShortName() + "?\nThis backlog has no stories in it.";
            node.getChildren().add(new Label(deleteMessage));
            checkBox = null;
        }

        final String[] buttons = {"Delete Backlog", "Cancel"};
        final String result = GoatDialog.createCustomNodeDialog(primaryStage, "Delete Backlog",
                "Are you sure? ", node, buttons);

        // change this because its hasn't been init yet
        final boolean deleteStories = (checkBox != null) ? checkBox.selectedProperty().getValue() : false;

        if (result.equals("Delete Backlog")) {
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (deleteStories) {
                final DeleteBacklogCommand command = new DeleteBacklogCommand(backlog);
                command.setDeleteMembers();
                changes.add(command);
            } else {
                changes.add(new DeleteBacklogCommand(backlog));
                // move all stories in backlog to stoies for project
                for (final Story story : backlog.getStories()) {
                    final MoveItemCommand<Story> command = new MoveItemCommand<>(story, backlog.observableStories(),
                    backlog.getProject().observableUnallocatedStories());
                    changes.add(command);
                }
            }
            doCommand(new CompoundCommand("Delete Backlog", changes));
        }
    }

    /**
     * Convenience method to delete the currently selected Item
     */
    public void deleteItem() {
        Platform.runLater(() -> {
            final Item focusedObject = focusedItemProperty.get();
            if (focusedObject instanceof Project) {
                deleteProject((Project) focusedObject);
            } else if (focusedObject instanceof Person) {
                deletePerson((Person) focusedObject);
            } else if (focusedObject instanceof Skill) {
                deleteSkill((Skill) focusedObject);
            } else if (focusedObject instanceof Team) {
                deleteTeam((Team) focusedObject);
            } else if (focusedObject instanceof Release) {
                deleteRelease((Release) focusedObject);
            } else if (focusedObject.getClass() == Story.class) {
                deleteStory((Story) focusedObject);
            } else if (focusedObject.getClass() == Backlog.class) {
                deleteBacklog((Backlog) focusedObject);
            }
        });
    }

    /**
     * Convenience method to edit the currently selected Item
     * (Note that the PO and SM skills can not be edited)
     */
    public void editItem() {
        final Item focusedObject = focusedItemProperty.get();
        if (focusedObject != null && focusedObject.getClass() == Skill.class) {
            if (focusedObject == selectedOrganisationProperty().get().getPoSkill() ||
                    focusedObject == selectedOrganisationProperty().get().getSmSkill()) {
                GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                        "The Product Owner and Scrum Master skills cannot be edited.");
                return;
            }
        }
        dialog(focusedObject);
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

        saveStateChanges();
        menuBarController.setListenersOnUndoManager(undoManager);
        focusedItemProperty.addListener((observable, oldValue, newValue) -> {
            MainController.LOGGER.log(Level.FINE, "Focus changed to %s", newValue);
            detailsPaneController.showDetailsPane(newValue);
        });

        selectedOrganisationProperty.addListener((observable, oldValue, newValue) -> {
            undoManager.empty();
        });
    }

    public ObjectProperty<Organisation> selectedOrganisationProperty() {
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
            // Check to make sure at least one project exists first, otherwise show warning dialog
            if (selectedOrganisationProperty.get().getProjects().isEmpty()) {
                GoatDialog.showAlertDialog(primaryStage, "Can't create Release", "Can't create Release",
                        "No projects available, you must first have a project in order to create a Release.");
                return;
            }
            dialog(null, "Release");
        }
    }

    public void newBacklog() {
        if (selectedOrganisationProperty.get() != null) {
            if (selectedOrganisationProperty.get().getProjects().isEmpty() ||
                    selectedOrganisationProperty.get().getPeople().isEmpty() ||
                    selectedOrganisationProperty.get().getEligiblePOs().isEmpty()) {
                GoatDialog.showAlertDialog(primaryStage, "Can't create Backlog", "Can't create Backlog",
                        "You must have at least one Project and one Person with the PO skill in order to create a Backlog.");
                return;
            }
            dialog(null, "Backlog");
        }
    }

    public void newStory() {
        if (selectedOrganisationProperty.get() != null) {
            if (selectedOrganisationProperty.get().getProjects().isEmpty() ||
                    selectedOrganisationProperty.get().getPeople().isEmpty()) {
                GoatDialog.showAlertDialog(primaryStage, "Can't create Story", "Can't create Story",
                        "You must have at least one Project and one Person in order to create a Story.");
                return;
            }
            dialog(null, "Story");
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
        Organisation organisation;
        try {
            organisation = PersistenceManager.loadOrganisation(filePath);
            selectedOrganisationProperty.set(organisation);
            // Empty the undo/redo stack(s)
            undoManager.empty();
            // Store the organisation as it currently stands
        } catch (ClassCastException | JsonSyntaxException | InvalidProjectException e) {
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
     * Optionally prompts the user for a new save location.
     * Updates the organisation's current save location.
     * Saves the current organisation to it.
     *
     * @param saveAs force user to select a save location
     */
    public boolean saveOrganisation(boolean saveAs) {
        final Organisation organisation = selectedOrganisationProperty().get();

        // ask for save location
        if (saveAs || organisation.getSaveLocation() == null) {
            final File file = promptForSaveLocation(organisation.getSaveLocation());
            if (file != null) {
                organisation.setSaveLocation(file);
            } else {
                return false;
            }
        }

        if (organisation.getSaveLocation() != null) { // if not cancelled
            saveToDisk(organisation);
        }
        return true;
    }

    /**
     * Saves the organisation to its save location (assumed not to be null).
     * @param organisation Organisation to be saved
     */
    private void saveToDisk(final Organisation organisation) {
        // do the save
        try {
            PersistenceManager.saveOrganisation(organisation.getSaveLocation(), organisation);
        } catch (final IOException e) {
            GoatDialog.showAlertDialog(primaryStage, "Save failed", "No can do.", "Somehow, that file didn't allow saving.");
            return; // do not continue
        }
        undoManager.markSavePosition();
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

    /**
     * Displays status report dialog
     */
    public void statusReport() {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("Report Generator");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("forms/report.fxml"));
            Pane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final ReportFormController reportFormController = loader.getController();

            reportFormController.setStage(stage);
            reportFormController.setOrganisation(selectedOrganisationProperty.get());
//            reportFormController.loadProject(project);

            stage.showAndWait();
            if (reportFormController.isValid()) {
                if (!reportFormController.getLevel().equals(ReportFormController.Level.ORGANISATION)) {
                    saveStatusReport(reportFormController.getTargetList());
                } else {
                    saveStatusReport(null);
                }
            }
        });
    }

    public void saveStatusReport(Collection<Item> list) {
        final String FILE_EXTENSION = ".yaml";

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("yaml Files", "*" + FILE_EXTENSION));
        final File existingFile = selectedOrganisationProperty.get().getSaveLocation();
        if (existingFile != null) {
            fileChooser.setInitialDirectory(existingFile.getParentFile());
            fileChooser.setInitialFileName(selectedOrganisationProperty.get().organisationNameProperty().get());
        }

        File selectedFile = fileChooser.showSaveDialog(primaryStage);

        if (selectedFile != null) {
            if (!selectedFile.getName().endsWith(FILE_EXTENSION)) {
                // append file extension if not already present
                selectedFile = new File(selectedFile.getAbsolutePath() + FILE_EXTENSION);
            }
            try (final FileWriter fileWriter = new FileWriter(selectedFile)) {
                final ReportGenerator reportGenerator = new ReportGenerator(selectedOrganisationProperty.get());
                if (list != null) {
                    fileWriter.write(reportGenerator.generateReport(list));
                } else {
                    fileWriter.write(reportGenerator.generateReport());
                }
            } catch (final IOException e) {
                MainController.LOGGER.log(Level.SEVERE, "Can't save status report", e);
            }
        }
    }

    public void undo() {
        undoManager.undoCommand();
    }

    public void redo() {
        undoManager.redoCommand();
    }

    public void doCommand(Command<?> command) {
        undoManager.doCommand(command);
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
     * Saves the project to disk and marks project as saved.
     *
     * @param existingFile initial directory and filename to suggest
     * @return file to save in (may be null if cancelled)
     */
    private File promptForSaveLocation(File existingFile) {
        final String FILE_EXTENSION = ".json";

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
        if (existingFile != null) {
            fileChooser.setInitialDirectory(existingFile.getParentFile());
            fileChooser.setInitialFileName(existingFile.getName());
        }
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null && !file.getName().endsWith(FILE_EXTENSION)) {
            // append file extension if not already present
            file = new File(file.getAbsolutePath() + FILE_EXTENSION);
        }
        return file;
    }

     /**
     * Prompt the user if they want to save unsaved changes
     * @return if the user clicked cancel or not
     */
    public boolean promptBeforeRevert() {
        if (!undoManager.changesSavedProperty().get()) {
            final String[] options = {"Revert", "Cancel"};
            final String response = GoatDialog.createBasicButtonDialog(primaryStage, "Revert Project", "You have unsaved changes.",
                    "\"File > Save As\" before reverting or you will lose these changes.", options);
            if (response.equals("Revert")) {
                revert();
            } else {
                // do nothing
                return false;
            }
        }
        return true;
    }

    /**
     * Prompt the user if they want to save unsaved changes
     * @return if the user clicked cancel or not
     */
    private boolean promptForUnsavedChanges() {
        if (!undoManager.changesSavedProperty().get()) {
            final String[] options = {"Save changes", "Discard changes", "Cancel"};
            final String response = GoatDialog.createBasicButtonDialog(primaryStage, "Save Project", "You have unsaved changes.",
                    "Would you like to save the changes you have made to the project?", options);
            switch (response) {
                case "Save changes":
                    return saveOrganisation(false);
                case "Discard changes":
                    return true;
                default:
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
        undoManager.changesSavedProperty().addListener((observable, oldValue, newValue) -> {
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
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setTitle(t == null ? "New " : "Edit " + type);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("forms/" + type.toLowerCase() + ".fxml"));
            Pane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                MainController.LOGGER.log(Level.SEVERE, "Can't load fxml", e);
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            @SuppressWarnings("unchecked")
            final FormController<T> formController = loader.getController();
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
            stage.initStyle(StageStyle.DECORATED);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("forms/allocation.fxml"));
            Pane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                MainController.LOGGER.log(Level.SEVERE, "Can't load fxml", e);
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

    public MainDetailsPaneController getDetailsPaneController() {
        return detailsPaneController;
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
