package seng302.group4.viewModel;

import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.*;
import javafx.util.Callback;
import org.controlsfx.control.StatusBar;
import seng302.group4.*;
import seng302.group4.exceptions.InvalidPersonException;
import seng302.group4.exceptions.InvalidProjectException;
import seng302.group4.undo.*;
import seng302.group4.utils.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private final UndoManager undoManager = new UndoManager();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final StatusBar statusBar = new StatusBar();
    final private String ALL_CHANGES_SAVED_TEXT = "All changes saved.";
    final private String UNSAVED_CHANGES_TEXT = "You have unsaved changes.";
    private final SimpleBooleanProperty changesSaved = new SimpleBooleanProperty(true);
    private Stage primaryStage;
    private AnchorPane listAnchorPane;
    private double dividerPosition;
    // FXML Injections
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
    private ListView<Release> releasesListView;
    @FXML
    private Tab projectTab;
    @FXML
    private Tab peopleTab;
    @FXML
    private Tab skillsTab;
    @FXML
    private Tab teamsTab;
    @FXML
    private Tab releasesTab;
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
    private Project selectedProject;
    private Person selectedPerson;
    private Skill selectedSkill;
    private Team selectedTeam;
    private Release selectedRelease;

    /**
     * Triggers an update of a specific object in a list view, so that updateItem is called and the
     * cell is recreated with current data (ie. if the short name changes in this case).
     * @param newValue Object in the list that has changed
     * @param listView ListView that object belongs to
     * @param <T> Type of the object
     */
    public static <T> void triggerListUpdate(T newValue, ListView<T> listView) {
        int i = listView.getItems().indexOf(newValue);
        EventType<? extends ListView.EditEvent<T>> type = ListView.editCommitEvent();
        Event event = new ListView.EditEvent<>(listView, type, newValue, i);
        listView.fireEvent(event);
        listView.getSelectionModel().select(newValue);
    }

    public void deleteSkill() {
        if (selectedSkill != null) {
            DeleteSkillCommand command = new DeleteSkillCommand(selectedSkill, selectedProject);

            String deleteMessage = "There are no people with this skill.";
            if (command.getPeopleWithSkill().size() > 0) {
                deleteMessage = "Deleting the skill will also remove it from the following people:\n";
                deleteMessage += Utilities.concatenatePeopleList(command.getPeopleWithSkill(), 5);
            }
            String[] buttons = {"Delete Skill", "Cancel"};
            String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Skill",
                    "Are you sure you want to delete the skill " + selectedSkill.getShortName() + "?",
                    deleteMessage, buttons);

            if (result.equals("Delete Skill")) {
                undoManager.doCommand(command);
            }
        }
    }

    public void deleteTeam() {
        if (selectedTeam != null) {
            // TODO Command stuff relating to DeleteTeamCommand

            VBox node = new VBox();
            node.setSpacing(10);

            CheckBox checkbox;

            if (true /* team has people in it */) {
                checkbox = new CheckBox("Also delete the people belonging to this team");
                String deleteMessage = "Deleting the skill will also remove it from the following people:\n";
                deleteMessage += Utilities.concatenatePeopleList(selectedTeam.getTeamMembers(), 5);
                node.getChildren().add(new Label(deleteMessage));

                node.getChildren().add(checkbox);
            } else {
                node.getChildren().add(new Label("This team has nobody in it."));
            }

            String[] buttons = {"Delete Team", "Cancel"};
            String result = GoatDialog.createCustomNodeDialog(primaryStage, "Test", "Heading", node, buttons);

            boolean deletePeople = (checkbox != null) ? checkbox.selectedProperty().getValue() : false;

            if (result.equals("Delete Team")) {
                // Then delete the team
                // The result of whether or not to delete the team members can be fetched by deletePeople boolean
                // command.setDeleteMembers(deletePeople)
                System.out.println("Delete people as well? " + deletePeople);
                //undoManager.doCommand(command);
            }
        }
    }

    public void deletePerson() {
        if (selectedPerson != null) {
            // DeletePersonCommand command = new DeletePersonCommand(selectedPerson, selectedProject);

            String[] buttons = {"Delete Person", "Cancel"};
            String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Person",
                    "Are you sure?",
                    "Are you sure you want to delete the person: " + selectedPerson.getShortName() + "?", buttons);

            if (result.equals("Delete Person")) {
                // Then do the command to delete the person
                // undoManager.doCommand(command);
            }
        }
    }

    public void deleteRelease() {
        if (selectedRelease != null) {
            DeleteReleaseCommand command = new DeleteReleaseCommand(selectedRelease, selectedProject);

            String[] buttons = {"Delete Release", "Cancel"};
            String result = GoatDialog.createBasicButtonDialog(primaryStage, "Delete Release",
                    "Are you sure you want to delete the release " + selectedRelease.getId() + "?",
                    "sometext", buttons);

            if (result.equals("Delete Release")) {
                undoManager.doCommand(command);
            }
        }
    }


    public void editSkill() {
        if (selectedSkill != null) {
            editSkillDialog(selectedSkill);
        }
    }

    public void editPerson() {
        if (selectedPerson != null) {
            editPersonDialog(selectedPerson);
        }
    }

    public void editTeam() {
        if (selectedTeam != null) {
            teamDialog(selectedTeam);
        }
    }

    public void editProject() {
        if (selectedProject != null) {
            editProjectDialog(selectedProject);
        }
    }

    public void editRelease() {
        if(selectedRelease != null) {
            releaseDialog(selectedRelease);
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

        initialiseProjectListView();
        initialisePeopleListView();
        initialiseSkillsListView();
        initialiseTeamsListView();
        initialiseReleaseListView();
        initialiseTabs();
        addStatusBar();
        menuBarController.setListenersOnUndoManager(undoManager);
    }

    private void initialiseTabs() {
        tabViewPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == projectTab) {
                projectListView.getSelectionModel().select(null);
                if (selectedProject != null) {
                    projectListView.getSelectionModel().select(null);
                    projectListView.getSelectionModel().select(selectedProject);
                } else {
                    projectListView.getSelectionModel().selectFirst();
                }
                detailsPaneController.showDetailsPane(projectListView.getSelectionModel().getSelectedItem());
                menuBarController.updateAfterProjectListSelected(true);
            } else if (newValue == peopleTab) {
                // Select the first person, if no person is selected already
                if (selectedPerson == null) {
                    peopleListView.getSelectionModel().select(null);
                    peopleListView.getSelectionModel().selectFirst();
                } else {
                    peopleListView.getSelectionModel().select(null);
                    peopleListView.getSelectionModel().select(selectedPerson);
                }
                detailsPaneController.showDetailsPane(peopleListView.getSelectionModel().getSelectedItem());
                menuBarController.updateAfterPersonListSelected(true);
            } else if (newValue == skillsTab) {
                if (selectedSkill == null) {
                    skillsListView.getSelectionModel().select(null);
                    skillsListView.getSelectionModel().selectFirst();
                } else {
                    skillsListView.getSelectionModel().select(null);
                    skillsListView.getSelectionModel().select(selectedSkill);
                }
                detailsPaneController.showDetailsPane(skillsListView.getSelectionModel().getSelectedItem());
                menuBarController.updateAfterSkillListSelected(true);
            } else if (newValue == teamsTab) {
                if (selectedTeam == null) {
                    teamsListView.getSelectionModel().select(null);
                    teamsListView.getSelectionModel().selectFirst();
                } else {
                    teamsListView.getSelectionModel().select(null);
                    teamsListView.getSelectionModel().select(selectedTeam);
                }
                detailsPaneController.showDetailsPane(teamsListView.getSelectionModel().getSelectedItem());
                menuBarController.updateAfterTeamListSelected(true);
            } else if (newValue == releasesTab) {
                if (selectedRelease == null) {
                    releasesListView.getSelectionModel().select(null);
                    releasesListView.getSelectionModel().selectFirst();
                } else {
                    releasesListView.getSelectionModel().select(null);
                    releasesListView.getSelectionModel().select(selectedRelease);
                }
                detailsPaneController.showDetailsPane(releasesListView.getSelectionModel().getSelectedItem());
                menuBarController.updateAfterReleasesListSelected(true);
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

    public void newRelease() {
        if (selectedProject != null) {
            releaseDialog(null);
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

    public void dragAndDrop(File filePath) {
        if (selectedProject != null) {
            GoatDialog.showAlertDialog(primaryStage, "Version Limitation", "No can do.",
                    "Only one project at a time is supported in this version.");
            return;
        }

        if (filePath == null) {
            return;
        }
        Project project = null;
        try {
            project = PersistenceManager.loadProject(filePath);
        } catch (JsonSyntaxException | InvalidProjectException e) {
            GoatDialog.showAlertDialog(primaryStage, "Error Loading Project", "No can do.",
                    "The JSON file you supplied is invalid.");
        } catch (final InvalidPersonException e) {
            GoatDialog.showAlertDialog(primaryStage, "Person Invalid", "No can do.",
                    "An invalid person was found.");
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            GoatDialog.showAlertDialog(primaryStage, "File Not Found", "No can do.",
                    "Somehow, the file you tried to open was not found.");
            e.printStackTrace();
        }
        if (project != null) {
            project.setSaveLocation(filePath);
            addProject(project);
            System.out.println(project.getShortName() + " has been loaded successfully");
        }
        tabViewPane.getSelectionModel().select(projectTab);
    }

    public void openProject() {
        if (selectedProject != null) {
            GoatDialog.showAlertDialog(primaryStage, "Version Limitation", "No can do.",
                    "Only one project at a time is supported in this version.");
            return;
        }

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
        final File filePath = fileChooser.showOpenDialog(primaryStage);

        if (filePath == null) {
            return;
        }
        Project project = null;
        try {
            project = PersistenceManager.loadProject(filePath);
        } catch (JsonSyntaxException | InvalidProjectException e) {
            GoatDialog.showAlertDialog(primaryStage, "Error Loading Project", "No can do.",
                    "The JSON file you supplied is invalid.");
        } catch (final InvalidPersonException e) {
            GoatDialog.showAlertDialog(primaryStage, "Person Invalid", "No can do.",
                    "An invalid person was found.");
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            GoatDialog.showAlertDialog(primaryStage, "File Not Found", "No can do.",
                    "Somehow, the file you tried to open was not found.");
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

    public void switchToReleaseList() {
        tabViewPane.getSelectionModel().select(releasesTab);
    }

    public void undo() {
        undoManager.undoCommand();
        // If the changes are already saved, and we undo something, then the
        // changes are now not saved
        if (changesSaved.get()) {
            changesSaved.set(false);
        }
    }


    public void redo() {
        undoManager.redoCommand();
        // If the changes are already saved, and we redo something, then the
        // changes are now not saved
        if (changesSaved.get()) {
            changesSaved.set(false);
        }
    }

    /**
     * Set save prompt when to handle request to close application
     */
    private void addClosePrompt() {
        primaryStage.setOnCloseRequest(event -> {
            if (!changesSaved.get()) {
                final String[] options = {"Save changes", "Discard changes", "Cancel"};
                final String response = GoatDialog.createBasicButtonDialog(primaryStage, "Save Project",
                        "You have unsaved changes.", "Would you like to save the changes you have made to the project?",
                        options);
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
            menuBarController.enableNewRelease();

            switchToProjectList();
            menuBarController.updateAfterProjectListSelected(true);
            saveProject();
        }
    }

    /**
     * Set up the status bar for the application and monitor for changes in the
     * save state
     */
    private void addStatusBar() {
        // Add the status bar to the bottom of the window
        mainBorderPane.setBottom(statusBar);

        // Set up listener for save status
        changesSaved.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If changes are saved, then update message to reflect that
                statusBar.setText(ALL_CHANGES_SAVED_TEXT);
            } else {
                // Then there are unsaved changes, update status message
                statusBar.setText(UNSAVED_CHANGES_TEXT);
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
                CompoundCommand command = editSkillController.getCommand();
                command.setType("Edit Skill");
                command.setRefreshParameters(skill, skillsListView);
                undoManager.doCommand(command);
            }
        });
    }

    private void editPersonDialog(Person person) {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
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
                CompoundCommand command = editPersonController.getCommand();
                command.setType("Edit Person");
                command.setRefreshParameters(person, peopleListView);
                undoManager.doCommand(command);
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
                CompoundCommand command = editProjectController.getCommand();
                command.setType("Edit Project");
                command.setRefreshParameters(project, projectListView);
                undoManager.doCommand(command);
            }
        });
    }

    /**
     * Sets the content for the main list view
     */
    private void initialiseProjectListView() {
        // derived from example at
        // http://docs.oracle.com/javafx/2/api/javafx/scene/control/Cell.html
        projectListView.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(final ListView<Project> arg0) {
                return new ListCell<Project>() {
                    @Override
                    protected void updateItem(final Project project, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(project, empty);
                        setText(empty ? "" : project.getShortName());
                    }
                };
            }
        });
        projectListView.setItems(projects);
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Project");
        contextMenu.getItems().add(editContextMenu);

        projectListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editProject());

        // Set change listener for projectListView
        projectListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailsPaneController.showDetailsPane(newValue);
            if (newValue != null) {
                selectedProject = newValue;

                // Set observable list of people, skills and teams corresponding to this new project
                peopleListView.setItems(selectedProject.getPeople());
                skillsListView.setItems(selectedProject.getSkills());
                teamsListView.setItems(selectedProject.getTeams());
                releasesListView.setItems(selectedProject.getRelease());

                // Update status bar to show current save status of selected project
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

                menuBarController.updateAfterProjectSelected(selectedProject != null);
                listLabel.setText((selectedProject != null) ? selectedProject.getShortName() : null);
            }
        });
    }

    /**
     * Sets the content for the projects list view
     */
    private void initialisePeopleListView() {
        peopleListView.setCellFactory(new Callback<ListView<Person>, ListCell<Person>>() {
            @Override
            public ListCell<Person> call(final ListView<Person> arg0) {
                return new ListCell<Person>() {
                    @Override
                    protected void updateItem(final Person person, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(person, empty);
                        setText(empty ? "" : person.getShortName());
                    }
                };
            }
        });

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Person");
        final MenuItem deleteContextMenu = new MenuItem("Delete Person");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);

        peopleListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editPerson());
        deleteContextMenu.setOnAction(event -> deletePerson());

        // Set change listener for projectListView
        peopleListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailsPaneController.showDetailsPane(newValue);
            if (newValue != null) {
                selectedPerson = newValue;

                // Update status bar to show current save status of selected
                // project
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

                menuBarController.updateAfterPersonSelected(true);
            }
        });
    }

    /**
     * Sets the content for the skills list view
     */
    private void initialiseSkillsListView() {
        skillsListView.setCellFactory(view -> new ListCell<Skill>() {
            @Override
            public void updateItem(Skill skill, boolean empty) {
                super.updateItem(skill, empty);
                setText(empty ? null : skill.getShortName());
            }
        });

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Skill");
        final MenuItem deleteContextMenu = new MenuItem("Delete Skill");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);

        skillsListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editSkill());
        deleteContextMenu.setOnAction(event -> deleteSkill());

        // Set change listener for projectListView
        skillsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailsPaneController.showDetailsPane(newValue);
            if (newValue != null) {
                selectedSkill = newValue;

                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

                menuBarController.updateAfterSkillSelected(true);
            }
        });
    }

    private void initialiseTeamsListView() {
        teamsListView.setCellFactory(new Callback<ListView<Team>, ListCell<Team>>() {
            @Override
            public ListCell<Team> call(final ListView<Team> arg0) {
                return new ListCell<Team>() {
                    @Override
                    protected void updateItem(final Team team, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(team, empty);
                        setText(empty ? "" : team.getShortName());
                    }
                };
            }
        });

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Team");
        final MenuItem deleteContextMenu = new MenuItem("Delete Team");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);

        teamsListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editTeam());
        deleteContextMenu.setOnAction(event -> deleteTeam());

        // Set change listener for projectListView
        teamsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                selectedTeam = newValue;
                // Update status bar to show current save status of selected
                // project
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

                menuBarController.updateAfterTeamSelected(true);

                detailsPaneController.showDetailsPane(selectedTeam);
            }
        });
    }

    public void initialiseReleaseListView() {
        releasesListView.setCellFactory(new Callback<ListView<Release>, ListCell<Release>>() {
            @Override
            public ListCell<Release> call(final ListView<Release> arg0) {
                return new ListCell<Release>() {
                    @Override
                    protected void updateItem(final Release release, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(release, empty);
                        setText(empty ? "" : release.getId());
                    }
                };
            }
        });

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Release");
        final MenuItem deleteContextMenu = new MenuItem("Delete Release");
        contextMenu.getItems().add(editContextMenu);
        contextMenu.getItems().add(deleteContextMenu);

        releasesListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editRelease());
        deleteContextMenu.setOnAction(event -> deleteRelease());

        // Set change listener for releaseListView
        releasesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                selectedRelease = newValue;
                // Update status bar to show current save status of selected
                // project
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

                menuBarController.updateAfterReleaseSelected(true);
//                menuBarController.updateAfterReleasesListSelected(true);

                detailsPaneController.showDetailsPane(selectedRelease);
            }
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
                CreateSkillCommand command = newSkillController.getCommand();
                undoManager.doCommand(command);
            }
        });
    }

    private void newPersonDialog() {
        // Needed to wrap the dialog box in runLater due to the dialog box occasionally opening twice (known FX issue)
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
                CreatePersonCommand command = newPersonController.getCommand();
                undoManager.doCommand(command);
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
            teamFormController.setProject(selectedProject);
            teamFormController.setTeam(team);
            teamFormController.setListSelectionViewSettings();

            stage.showAndWait();
            if (teamFormController.isValid()) {
                if (team == null) {
                    // creating
                    // Create the command and do it
                    CreateTeamCommand command = (CreateTeamCommand) teamFormController.getCommand();
                    undoManager.doCommand(command);
                } else {
                    // editing

                    CompoundCommand command = (CompoundCommand) teamFormController.getCommand();
                    command.setType("Edit Team");
                    command.setRefreshParameters(team, teamsListView);
                    undoManager.doCommand(command);
                }
            }

        });
    }

    public void releaseDialog(Release release) {
        Platform.runLater(() -> {
            final Stage stage = new Stage();
            stage.initOwner(primaryStage);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            final FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainController.class.getClassLoader().getResource("dialogs/release.fxml"));
            BorderPane root;
            try {
                root = loader.load();
            } catch (final IOException e) {
                e.printStackTrace();
                return;
            }
            final Scene scene = new Scene(root);
            stage.setScene(scene);
            final ReleaseFormController releaseFormController = loader.getController();
            releaseFormController.setStage(stage);
            releaseFormController.setProject(selectedProject);
            releaseFormController.setRelease(release);

            stage.showAndWait();
            if (releaseFormController.isValid()) {
                if (release == null) {
                    CreateReleaseCommand command = (CreateReleaseCommand) releaseFormController.getCommand();
                    undoManager.doCommand(command);
                } else {
                    CompoundCommand command = (CompoundCommand) releaseFormController.getCommand();
                    command.setType("Edit Release");
                    command.setRefreshParameters(release, releasesListView);
                    undoManager.doCommand(command);
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
                CreateProjectCommand command = newProjectController.getCommand();
                addProject(command.execute());
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