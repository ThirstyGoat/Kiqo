package seng302.group4.viewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.controlsfx.control.StatusBar;

import seng302.group4.GoatDialog;
import seng302.group4.Item;
import seng302.group4.PersistenceManager;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Skill;
import seng302.group4.Team;
import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.CreatePersonCommand;
import seng302.group4.undo.CreateProjectCommand;
import seng302.group4.undo.CreateSkillCommand;
import seng302.group4.undo.CreateTeamCommand;
import seng302.group4.undo.DeletePersonCommand;
import seng302.group4.undo.DeleteSkillCommand;
import seng302.group4.undo.DeleteTeamCommand;
import seng302.group4.undo.UndoManager;
import seng302.group4.utils.Utilities;

import com.google.gson.JsonSyntaxException;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ListView<Project> projectListView;
    @FXML
    private ListView<Person> peopleListView;
    @FXML
    private ListView<Skill> skillsListView;
    @FXML
    private ListView<Team> teamsListView;
    @FXML
    private Tab projectTab;
    @FXML
    private Tab peopleTab;
    @FXML
    private Tab skillsTab;
    @FXML
    private Tab teamsTab;
    @FXML
    private TabPane tabViewPane;
    @FXML
    private SplitPane mainSplitPane;
    @FXML
    private Label listLabel;
    @FXML
    private Pane detailsPane;
    @FXML
    private DetailsPaneController detailsPaneController;
    @FXML
    private MenuBarController menuBarController;

    private static final String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    private static final String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";

    private final UndoManager undoManager = new UndoManager();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final SimpleBooleanProperty changesSaved = new SimpleBooleanProperty(true);
    private static final SimpleObjectProperty<Item> focusedItemProperty = new SimpleObjectProperty<>();

    private Stage primaryStage;
    private AnchorPane listAnchorPane;
    private double dividerPosition;

    private Project selectedProject;
    private Person selectedPerson;
    private Skill selectedSkill;
    private Team selectedTeam;

    /**
     * Triggers an update of a specific object in a list view, so that updateItem is called and the
     * cell is recreated with current data (ie. if the short name changes in this case).
     * @param newValue Object in the list that has changed
     * @param listView ListView that object belongs to
     * @param <T> Type of the object
     */
    public static <T> void triggerListUpdate(T newValue, ListView<T> listView) {
        final int i = listView.getItems().indexOf(newValue);
        final EventType<? extends ListView.EditEvent<T>> type = ListView.editCommitEvent();
        final Event event = new ListView.EditEvent<>(listView, type, newValue, i);
        listView.fireEvent(event);
        listView.getSelectionModel().select(newValue);
        if (listView.getItems().isEmpty()) {
            MainController.focusedItemProperty.set(null);
        } else {
            if (newValue == listView.getSelectionModel().getSelectedItem()) {
                listView.getSelectionModel().select(null);
            }
            listView.getSelectionModel().select(newValue);
        }
    }

    /**
     * @param project
     *
     */
    private void deleteProject(Project project) {
        GoatDialog
                .showAlertDialog(primaryStage, "Version Limitation", "No can do.", "Deleting a project is not supported in this version.");
    }

    private void deleteSkill(Skill skill) {
        if (skill == selectedProject.getPoSkill() || skill == selectedProject.getSmSkill()) {
            GoatDialog.showAlertDialog(primaryStage, "Prohibited Operation", "Not allowed.",
                    "The Product Owner and Scrum Master skills cannot be deleted.");
        } else {
            final DeleteSkillCommand command = new DeleteSkillCommand(skill, selectedProject);

            String deleteMessage = "There are no people with this skill.";
            if (command.getPeopleWithSkill().size() > 0) {
                deleteMessage = "Deleting the skill will also remove it from the following people:\n";
                deleteMessage += Utilities.concatenatePeopleList(command.getPeopleWithSkill(), 5);
            }
            final String[] buttons = { "Delete Skill", "Cancel" };
            final String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Skill",
                    "Are you sure you want to delete the skill " + skill.getShortName() + "?", deleteMessage, buttons);

            if (result.equals("Delete Skill")) {
                doCommand(command);
            }
        }
    }

    private void deleteTeam(Team team) {
        final DeleteTeamCommand command = new DeleteTeamCommand(team, selectedProject);

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
            if (deletePeople) {
                command.setDeleteMembers();
            }
            doCommand(command);
        }
    }

    private void deletePerson(Person person) {
        final DeletePersonCommand command = new DeletePersonCommand(selectedPerson, selectedProject);

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

    public void deleteItem() {
        final Item focusedObject = MainController.focusedItemProperty.get();
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
        }
    }

    public void editItem() {
        final Item focusedObject = MainController.focusedItemProperty.get();
        if (focusedObject == null) {
            // do nothing
        } else if (focusedObject instanceof Project) {
            editProjectDialog((Project) focusedObject);
        } else if (focusedObject instanceof Person) {
            editPersonDialog((Person) focusedObject);
        } else if (focusedObject instanceof Skill) {
            editSkillDialog((Skill) focusedObject);
        } else if (focusedObject instanceof Team) {
            teamDialog((Team) focusedObject);
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
        setLayoutProperties();
        initializeListViews();
        initialiseTabs();
        addStatusBar();
        menuBarController.setListenersOnUndoManager(undoManager);
        MainController.focusedItemProperty.addListener(new ChangeListener<Item>() {
            @Override
            public void changed(ObservableValue<? extends Item> observable, Item oldValue, Item newValue) {
                System.out.println("Focus changed to " + newValue);
                detailsPaneController.showDetailsPane(newValue);
                menuBarController.updateAfterAnyObjectSelected(newValue != null);
            }
        });
    }

    private void initializeListViews() {
        // Get a list of them
        final ArrayList<ListView<? extends Item>> listViews = new ArrayList<>();
        listViews.add(projectListView);
        listViews.add(peopleListView);
        listViews.add(skillsListView);
        listViews.add(teamsListView);

        // All these ListViews share a single context menu
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit");
        final MenuItem deleteContextMenu = new MenuItem("Delete");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);
        editContextMenu.setOnAction(event -> editItem());
        deleteContextMenu.setOnAction(event -> deleteItem());

        for (final ListView<? extends Item> listView : listViews) {
            initialiseListView(listView);
            listView.setContextMenu(contextMenu);
        }

        // do project-specific things
        augmentProjectListView();

        // set additional listeners so that the selection is retained despite
        // tab-switching
        peopleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedPerson = newValue;
            }
        });
        skillsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedSkill = newValue;
            }
        });
        teamsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedTeam = newValue;
            }
        });
    }

    private void initialiseTabs() {
        tabViewPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == projectTab) {
                projectListView.getSelectionModel().select(null);
                if (selectedProject != null) {
                    projectListView.getSelectionModel().select(selectedProject);
                } else {
                    projectListView.getSelectionModel().selectFirst();
                }
                if (projectListView.getItems().isEmpty()) {
                    MainController.focusedItemProperty.set(null);
                }
                menuBarController.updateAfterProjectListSelected(true);
            } else if (newValue == peopleTab) {
                peopleListView.getSelectionModel().select(null);
                if (selectedPerson == null) {
                    peopleListView.getSelectionModel().selectFirst();
                } else {
                    peopleListView.getSelectionModel().select(selectedPerson);
                }
                if (peopleListView.getItems().isEmpty()) {
                    MainController.focusedItemProperty.set(null);
                }
                menuBarController.updateAfterPersonListSelected(true);
            } else if (newValue == skillsTab) {
                skillsListView.getSelectionModel().select(null);
                if (selectedSkill == null) {
                    skillsListView.getSelectionModel().selectFirst();
                } else {
                    skillsListView.getSelectionModel().select(selectedSkill);
                }
                if (skillsListView.getItems().isEmpty()) {
                    MainController.focusedItemProperty.set(null);
                }
                menuBarController.updateAfterSkillListSelected(true);
            } else if (newValue == teamsTab) {
                teamsListView.getSelectionModel().select(null);
                if (selectedTeam == null) {
                    teamsListView.getSelectionModel().selectFirst();
                } else {
                    teamsListView.getSelectionModel().select(selectedTeam);
                }
                if (teamsListView.getItems().isEmpty()) {
                    MainController.focusedItemProperty.set(null);
                }
                menuBarController.updateAfterTeamListSelected(true);
            }
        });
    }

    public void newSkill() {
        if (selectedProject != null) {
            newSkillDialog();
        }
    }

    public void newPerson() {
        if (selectedProject != null) {
            newPersonDialog();
        }
    }

    public void newTeam() {
        if (selectedProject != null) {
            teamDialog(null);
        }
    }

    public void newProject() {
        if (selectedProject != null) {
            GoatDialog.showAlertDialog(primaryStage, "Version Limitation", "No can do.",
                    "Only one project at a time is supported in this version.");
            return;
        }
        newProjectDialog();
    }

    public void openProject(File draggedFilePath) {
        File filePath;

        if (selectedProject != null) {
            GoatDialog.showAlertDialog(primaryStage, "Version Limitation", "No can do.",
                    "Only one project at a time is supported in this version.");
            return;
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
        Project project = null;
        try {
            project = PersistenceManager.loadProject(filePath);
        } catch (JsonSyntaxException | InvalidProjectException e) {
            GoatDialog.showAlertDialog(primaryStage, "Error Loading Project", "No can do.", "The JSON file you supplied is invalid.");
        } catch (final InvalidPersonException e) {
            GoatDialog.showAlertDialog(primaryStage, "Person Invalid", "No can do.", "An invalid person was found.");
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            GoatDialog.showAlertDialog(primaryStage, "File Not Found", "No can do.", "Somehow, the file you tried to open was not found.");
            e.printStackTrace();
        }
        if (project != null) {
            project.setSaveLocation(filePath);
            addProject(project);
            System.out.println(project.getShortName() + " has been loaded successfully");
        }
        tabViewPane.getSelectionModel().select(projectTab);
    }

    /**
     * Saves the project to disk and marks project as saved.
     */
    public void saveProject() {
        final Project project = selectedProject;
        try {
            PersistenceManager.saveProject(project.getSaveLocation(), project);
        } catch (final IOException e) {
            e.printStackTrace();
            return;
        }
        changesSaved.set(true);
    }

    public void setListVisible(boolean visible) {
        if (visible) {
            // shows the list view
            mainSplitPane.getItems().add(0, listAnchorPane);
            mainSplitPane.setDividerPosition(0, dividerPosition);
        } else {
            // hides the list view
            dividerPosition = mainSplitPane.getDividerPositions()[0];
            mainSplitPane.getItems().remove(listAnchorPane);
        }
    }

    public void setPrimaryStage(final Stage primaryStage) {
        this.primaryStage = primaryStage;
        addClosePrompt();
        menuBarController.setMainController(this);
        detailsPaneController.setMainController(this);
    }

    public void switchToSkillList() {
        tabViewPane.getSelectionModel().select(skillsTab);
    }

    public void switchToPersonList() {
        tabViewPane.getSelectionModel().select(peopleTab);
    }

    public void switchToTeamList() {
        tabViewPane.getSelectionModel().select(teamsTab);
    }

    public void switchToProjectList() {
        tabViewPane.getSelectionModel().select(projectTab);
    }

    public void undo() {
        undoManager.undoCommand();
        changesSaved.set(false);
    }

    public void redo() {
        undoManager.redoCommand();
        // If the changes are already saved, and we redo something, then the
        // changes are now not saved
        changesSaved.set(false);
    }

    private void doCommand(Command<?> command) {
        undoManager.doCommand(command);
        changesSaved.set(false);
    }

    /**
     * Set save prompt when to handle request to close application
     */
    private void addClosePrompt() {
        primaryStage.setOnCloseRequest(event -> {
            if (!changesSaved.get()) {
                final String[] options = { "Save changes", "Discard changes", "Cancel" };
                final String response = GoatDialog.createBasicButtonDialog(primaryStage, "Save Project", "You have unsaved changes.",
                        "Would you like to save the changes you have made to the project?", options);
                if (response.equals("Save changes")) {
                    saveProject();
                } else if (response.equals("Cancel")) {
                    event.consume();
                }
            }
        });
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
            projectListView.getSelectionModel().select(null);
            projectListView.getSelectionModel().select(project);

            // enable menu items
            menuBarController.enableNewTeam();
            menuBarController.enableNewPerson();
            menuBarController.enableNewSkill();

            switchToProjectList();
            saveProject();
        }
    }

    /**
     * Set up the status bar for the application and monitor for changes in the
     * save state
     */
    private void addStatusBar() {
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

    private void editSkillDialog(Skill skill) {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("Edit Skill");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/editSkill.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final EditSkillController editSkillController = loader.getController();
            editSkillController.setStage(stage);
            editSkillController.loadSkill(skill);
            editSkillController.setProject(selectedProject);
            editSkillController.setProjectForFormController();

            stage.showAndWait();
            if (editSkillController.isValid()) {
                final CompoundCommand command = editSkillController.getCommand();
                command.setRefreshParameters(skill, skillsListView, detailsPaneController);
                doCommand(command);
            }
        });
    }

    private void editPersonDialog(Person person) {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("Edit Person");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/editPerson.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final EditPersonController editPersonController = loader.getController();
            editPersonController.setStage(stage);
            editPersonController.setProject(selectedProject);
            editPersonController.setProjectForFormController();
            editPersonController.loadPerson(person);

            stage.showAndWait();

            if (editPersonController.isValid()) {
                final CompoundCommand command = editPersonController.getCommand();
                command.setRefreshParameters(person, peopleListView, detailsPaneController);
                doCommand(command);
            }
        });
    }

    private void editProjectDialog(Project project) {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
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
                final CompoundCommand command = editProjectController.getCommand();
                command.setRefreshParameters(project, projectListView, detailsPaneController);
                doCommand(command);
            }
        });
    }

    /**
     * Attaches cell factory and selection listener to the list view.
     */
    private <T extends Item> void initialiseListView(ListView<T> listView) {
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
                    }
                };
                // TODO listCell.setContextMenu(contextMenu);
                return listCell;
            }
        });
        projectListView.setItems(projects);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                MainController.focusedItemProperty.set(newValue);

                // Update status bar to show current save status
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());
            }
        });
    }

    /**
     * Adds project-specific features to the project list view.
     */
    private void augmentProjectListView() {
        projectListView.setItems(projects);

        projectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedProject = newValue;

                // Set observable list of people, skills and teams corresponding
                // to this new project
                peopleListView.setItems(newValue.getPeople());
                skillsListView.setItems(newValue.getSkills());
                teamsListView.setItems(newValue.getTeams());

                // update label to display name of currently selected project
                listLabel.setText(newValue.getShortName());
            }
            // do this after updating focusedObject, to undo the "enable delete"
            // effect
            menuBarController.updateAfterProjectSelected(newValue != null);
        });
    }

    private void newSkillDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("New Skill");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newSkill.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final NewSkillController newSkillController = loader.getController();
            newSkillController.setStage(stage);
            newSkillController.setProject(selectedProject);

            stage.showAndWait();
            if (newSkillController.isValid()) {
                final CreateSkillCommand command = newSkillController.getCommand();
                doCommand(command);
            }
        });
    }

    private void newPersonDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("New Person");
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/newPerson.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final NewPersonController newPersonController = loader.getController();
            newPersonController.setStage(stage);
            newPersonController.setProject(selectedProject);
            newPersonController.setProjectForFormController();

            stage.showAndWait();
            if (newPersonController.isValid()) {
                final CreatePersonCommand command = newPersonController.getCommand();
                doCommand(command);
            }
        });
    }

    private void teamDialog(Team team) {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/team.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final TeamFormController teamFormController = loader.getController();
            teamFormController.setStage(stage);
            teamFormController.setTeam(team);
            teamFormController.setProject(selectedProject);
            teamFormController.setListSelectionViewSettings();

            stage.showAndWait();
            if (teamFormController.isValid()) {
                if (team == null) {
                    // creating
                    // Create the command and do it
                    final CreateTeamCommand command = (CreateTeamCommand) teamFormController.getCommand();
                    doCommand(command);
                } else {
                    // editing

                    final CompoundCommand command = (CompoundCommand) teamFormController.getCommand();
                    command.setRefreshParameters(team, teamsListView, detailsPaneController);
                    doCommand(command);
                }
            }
        });
    }

    private void newProjectDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box
        // occasionally opening twice (known FX issue)
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.setTitle("New Project");
            stage.initOwner(primaryStage);
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
                // TODO This will need work when we add support for multiple projects
                final CreateProjectCommand command = newProjectController.getCommand();
                addProject(command.execute()); // not undoable
            }
        });
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        listAnchorPane = (AnchorPane) mainSplitPane.getItems().get(0);
    }
}
