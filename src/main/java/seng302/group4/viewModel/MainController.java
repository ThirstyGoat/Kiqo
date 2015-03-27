package seng302.group4.viewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.controlsfx.control.StatusBar;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

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
import seng302.group4.undo.UndoManager;

import com.google.gson.JsonSyntaxException;

/**
 * Main controller for the primary view
 */
public class MainController implements Initializable {
    private final UndoManager undoManager = new UndoManager();
    private final ObservableList<Project> projects = FXCollections.observableArrayList();
    private final ObservableList<Person> people = FXCollections.observableArrayList();
    private final ObservableList<Skill> skills = FXCollections.observableArrayList();
    private final ObservableList<Team> teams = FXCollections.observableArrayList();
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
    private Project selectedProject;
    private Person selectedPerson;
    private Skill selectedSkill;
    private Team selectedTeam;

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
            Dialogs.create().owner(primaryStage).title("Error")
            .message("Currently, only one project at a time is supported in this version.").showWarning();
            return;
        } else {
            newProjectDialog();
        }
    }

    public void openProject() {
        if (selectedProject != null) {
            Dialogs.create().owner(primaryStage).title("Error")
            .message("Currently, only one project at a time is supported in this version.").showWarning();
            return;
        }

        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files(.JSON)", "*.json"));
        final File filePath = fileChooser.showOpenDialog(primaryStage);

        if (filePath == null) {
            return;
        }
        // TODO Actually do something with the selected file
        Project project = null;
        try {
            project = PersistenceManager.loadProject(filePath);
        } catch (JsonSyntaxException | InvalidProjectException e) {
            System.out.println("JSON file invalid");
            Dialogs.create().owner(primaryStage).title("Error")
                    .message("JSON file invalid").showWarning();
        } catch (final InvalidPersonException e) {
            System.out.println("Person invalid");
            e.printStackTrace();
        } catch (final FileNotFoundException e) {
            System.out.println("file not found");
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
                final Action response = Dialogs.create().owner(primaryStage).title("Save Project")
                        .masthead("You have unsaved changes.").message("Would you like to save the changes you have made to the project?")
                        .showConfirm();
                if (response == Dialog.ACTION_YES) {
                    saveProject();
                } else if (response == Dialog.ACTION_CANCEL) {
                    event.consume();
                }
            }
        });
    }

    private void addPersonToList(Person person) {
        if (person != null) {
            // Update view accordingly
            people.add(person);

            // Select added person in the listView
            peopleListView.getSelectionModel().select(null);
            peopleListView.getSelectionModel().select(person);
            switchToPersonList();
            menuBarController.updateAfterPersonListSelected(true);

            // Save the project
            saveProject();
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
            projectListView.getSelectionModel().select(null);
            projectListView.getSelectionModel().select(project);

            // enable menu itmes
            menuBarController.enableNewTeam();
            menuBarController.enableNewPerson();
            menuBarController.enableNewSkill();

            switchToProjectList();
            menuBarController.updateAfterProjectListSelected(true);
            saveProject();
        }
    }

    private void addTeam(final Team team) {
        if (team != null) {
            selectedProject.addTeam(team);
            teams.add(team);

            // Select added team in the listView
            teamsListView.getSelectionModel().select(null);
            teamsListView.getSelectionModel().select(team);

            switchToTeamList();
            menuBarController.updateAfterTeamListSelected(true);

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
                final Command<Skill> c = new Command<Skill>() {
                    CompoundCommand cc = editSkillController.getCommand();

                    @Override
                    public Skill execute() {
                        // Add to mainListView
                        cc.execute();
                        saveProject();
                        refreshList();
                        return null;
                    }

                    @Override
                    public void undo() {
                        cc.undo();
                        refreshList();
                    }

                    @Override
                    public String getType() {
                        return "Edit Skill";
                    }
                };

                undoManager.doCommand(c);
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
                final Command c = new Command() {
                    CompoundCommand cc = editPersonController.getCommand();

                    @Override
                    public Object execute() {
                        // Add to projectListView
                        cc.execute();
                        saveProject();
                        refreshList();
                        return null;
                    }

                    @Override
                    public String getType() {
                        return "Edit Person";
                    }

                    @Override
                    public void undo() {
                        // Remove from projectListView
                        cc.undo();
                        refreshList();
                    }
                };
                undoManager.doCommand(c);
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
                        // Add to projectListView
                        cc.execute();
                        saveProject();
                        refreshList();
                        return null;
                    }

                    @Override
                    public String getType() {
                        return "Edit Project";
                    }

                    @Override
                    public void undo() {
                        // Remove from projectListView
                        cc.undo();
                        refreshList();
                    }
                };
                undoManager.doCommand(c);
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

                // Set observable list of people and skills corresponding to this new project
                people.setAll(selectedProject.getPeople());
                skills.setAll(selectedProject.getSkills());
                teams.setAll(selectedProject.getTeams());

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
        peopleListView.setItems(people);

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Person");
        contextMenu.getItems().add(editContextMenu);

        peopleListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editPerson());

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
        skillsListView.setCellFactory(new Callback<ListView<Skill>, ListCell<Skill>>() {
            @Override
            public ListCell<Skill> call(final ListView<Skill> arg0) {
                return new ListCell<Skill>() {
                    @Override
                    protected void updateItem(final Skill skill, final boolean empty) {
                        // calling super here is very important
                        super.updateItem(skill, empty);
                        setText(empty ? "" : skill.getShortName());
                    }
                };
            }
        });

        skillsListView.setItems(skills);

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Skill");
        contextMenu.getItems().add(editContextMenu);

        skillsListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editSkill());

        // Set change listener for projectListView
        skillsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            detailsPaneController.showDetailsPane(newValue);
            if (newValue != null) {
                selectedSkill = newValue;

                // Update status bar to show current save status of selected
                // project
                // Probably not the best way to do this, but it's the simplest
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

        teamsListView.setItems(teams);

        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem editContextMenu = new MenuItem("Edit Team");
        contextMenu.getItems().add(editContextMenu);

        teamsListView.setContextMenu(contextMenu);

        editContextMenu.setOnAction(event -> editTeam());

        // Set change listener for projectListView
        teamsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {

                selectedTeam = newValue;
                // Update status bar to show current save status of selected
                // project
                // Probably not the best way to do this, but it's the simplest
                changesSaved.set(!changesSaved.get());
                changesSaved.set(!changesSaved.get());

//                menuBarController.updateAfterTeamSelected(true);

                detailsPaneController.showDetailsPane(selectedTeam);
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
                final Command<Skill> c = new Command<Skill>() {
                    private final CreateSkillCommand cpc = newSkillController.getCommand();

                    @Override
                    public Skill execute() {
                        // Add to mainListView
                        final Skill skill = cpc.execute();
                        addSkillToProject(skill);
                        return skill;
                    }

                    @Override
                    public void undo() {
                        // Remove from mainListView
                        removeSkillFromProject(cpc.getSkill());
                        cpc.undo();
                    }

                    @Override
                    public String getType() {
                        return cpc.getType();
                    }
                };

                undoManager.doCommand(c);
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
                final Command c = new Command() {
                    CreatePersonCommand cpc = newPersonController.getCommand();

                    @Override
                    public Object execute() {
                        // Add to projectListView
                        final Person person = cpc.execute();
                        selectedProject.addPerson(person);
                        addPersonToList(person);
                        return person;
                    }

                    @Override
                    public String getType() {
                        return cpc.getType();
                    }

                    @Override
                    public void undo() {
                        // Remove from projectListView
                        people.remove(cpc.getPerson());
                        selectedProject.removePerson(cpc.getPerson());
                        cpc.undo();
                    }
                };
                undoManager.doCommand(c);
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

            stage.showAndWait();
            if (teamFormController.isValid()) {
                Command c;
                if (team == null) {
                    // creating
                    // Create the command and do it
                    c = new Command<Team>() {
                        private final CreateTeamCommand ctc = (CreateTeamCommand) teamFormController.getCommand();

                        @Override
                        public Team execute() {
                            final Team team = ctc.execute();
                            addTeam(team);
                            return team;
                        }

                        @Override
                        public void undo() {
                            teams.remove(ctc.getTeam());
                            for (final Person person : ctc.getTeam().getTeamMembers()) {
                                refreshList();
                            }
                        }

                        @Override
                        public String getType() {
                            return ctc.getType();
                        }
                    };
                } else {
                    // editing
                    // Create the command and do it
                    c = new Command<Team>() {
                        private final CompoundCommand cc = (CompoundCommand) teamFormController.getCommand();

                        @Override
                        public Team execute() {
                            System.out.println(cc);
                            cc.execute();
                            saveProject();
                            refreshList();
                            return null;
                        }

                        @Override
                        public String getType() {
                            return "Edit Team";
                        }

                        @Override
                        public void undo() {
                            // Remove from projectListView
                            cc.undo();
                            refreshList();
                        }
                    };
                }
                undoManager.doCommand(c);
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
                final Command<Project> c = new Command<Project>() {
                    private final CreateProjectCommand cpc = newProjectController.getCommand();

                    @Override
                    public Project execute() {
                        // Add to projectListView
                        final Project project = cpc.execute();
                        addProject(project);
                        return project;
                    }

                    @Override
                    public String getType() {
                        return cpc.getType();
                    }

                    @Override
                    public void undo() {
                        // Remove from projectListView
                        projects.remove(cpc.getProject());
                        cpc.undo();
                    }
                };

                // this.undoManager.doCommand(c);
                // We don't do the command, since it is not meant to be undoable
                // at this stage
                c.execute();
            }
        });
    }

    private void removeSkillFromProject(Skill skill) {
        selectedProject.getSkills().remove(skill);
        skills.remove(skill);
    }

    private void addSkillToProject(Skill skill) {
        selectedProject.addSkill(skill);
        // Update listView and select newly added skill
        skills.add(skill);
        skillsListView.getSelectionModel().select(skill);
        menuBarController.updateAfterSkillSelected(true);
        switchToSkillList();
        saveProject();
    }

    /**
     * Forces a redraw of the list view (and the detailsPane)
     */
    private void refreshList() {
        if (projectTab.isSelected()) {
            projectListView.setItems(null);
            projectListView.setItems(projects);
            projectListView.getSelectionModel().select(null);
            projectListView.getSelectionModel().select(selectedProject);
        } else if (peopleTab.isSelected()) {
            peopleListView.setItems(null);
            peopleListView.setItems(people);
            peopleListView.getSelectionModel().select(null);
            peopleListView.getSelectionModel().select(selectedPerson);
        } else if (skillsTab.isSelected()) {
            skillsListView.setItems(null);
            skillsListView.setItems(skills);
            skillsListView.getSelectionModel().select(null);
            skillsListView.getSelectionModel().select(selectedSkill);
        } else if (teamsTab.isSelected()) {
            teamsListView.setItems(null);
            teamsListView.setItems(teams);
            teamsListView.getSelectionModel().select(null);
            teamsListView.getSelectionModel().select(selectedTeam);
        }
    }

    /**
     * Sets layout specific properties
     */
    private void setLayoutProperties() {
        listAnchorPane = (AnchorPane) mainSplitPane.getItems().get(0);
    }
}
