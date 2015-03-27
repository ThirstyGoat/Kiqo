package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;
import seng302.group4.customNodes.GoatListSelectionView;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.CreateTeamCommand;
import seng302.group4.undo.EditCommand;

/**
 * Created by james on 27/03/15.
 */
public class TeamFormController implements Initializable {

    // Begin FXML Injections
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private GoatListSelectionView<Person> peopleListSelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Stage stage;
    private Project project;
    private Team team;
    private Command command;
    private boolean valid = false;

    private final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
    private final ObservableList<Person> targetPeople = FXCollections.observableArrayList();
    private final PopOver errorPopOver = new PopOver();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setListSelectionViewSettings();
        setTextFieldListener();

        Platform.runLater(shortNameTextField::requestFocus);
    }

    private void setTextFieldListener() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // then focus is on this text field
                errorPopOver.hide();
            }
        });
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                errorPopOver.hide();
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            errorPopOver.hide();
            stage.close();
        });
    }

    private boolean validate() {
        if (shortNameTextField.getText().length() == 0) {
            errorPopOver.setContentNode(new Label("Short name must not be empty"));
            errorPopOver.show(shortNameTextField);
            return false;
        }

        if (team != null) {
            // we're editing
            if (shortNameTextField.getText().equals(team.getShortName())) {
                // then that's fine
                valid = true;
                setCommand();
                return true;
            }
        }
        for (final Team t : project.getTeams()) {
            if (shortNameTextField.getText().equals(t.getShortName())) {
                errorPopOver.setContentNode(new Label("Short name must be unique"));
                errorPopOver.show(shortNameTextField);
                return false;
            }
        }
        valid = true;
        setCommand();
        return true;
    }

    private void setCommand() {
        final ArrayList<Person> teamMembers = new ArrayList<>();
        teamMembers.addAll(targetPeople);
        if (team == null) {
            // create command
            command = new CreateTeamCommand(shortNameTextField.getText(), descriptionTextField.getText(), teamMembers);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();

            if (!shortNameTextField.getText().equals(team.getShortName())) {
                changes.add(new EditCommand<>(team, "shortName", shortNameTextField.getText()));
            }
            if (!descriptionTextField.getText().equals(team.getDescription())) {
                changes.add(new EditCommand<>(team, "description", descriptionTextField.getText()));
            }
            if (!(teamMembers.containsAll(team.getTeamMembers()) && team.getTeamMembers().containsAll(teamMembers))) {
                changes.add(new EditCommand<>(team, "teamMembers", teamMembers));
            }

            valid = !changes.isEmpty();
            command = new CompoundCommand(changes);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public Command getCommand() {
        return command;
    }

    private void setListSelectionViewSettings() {
        peopleListSelectionView.setSourceHeader(new Label("People Available:"));
        peopleListSelectionView.setTargetHeader(new Label("People Selected:"));

        peopleListSelectionView.setPadding(new Insets(0, 0, 0, 0));

        // Set the custom cell factory for the skills lists
        // Thank GoatListSelectionView for this fabulous method

        setCellFactory(peopleListSelectionView.getSourceListView());
        setCellFactory(peopleListSelectionView.getTargetListView());
    }

    private void setCellFactory(ListView<Person> listView) {
        listView.setCellFactory(view -> {
            final ListCell<Person> cell = new ListCell<Person>() {
                @Override
                public void updateItem(Person person, boolean empty) {
                    super.updateItem(person, empty);
                    if (person != null) {
                        setText(person.getShortName());
                    } else {
                        setText(null);
                    }
                }
            };
            return cell;
        });
    }

    private void populatePeopleListView() {
<<<<<<< HEAD
        final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
=======
        final ArrayList<Person> allPeople = new ArrayList<>();
        // Populate allPeople with all people in the project
        for (final Person person : project.getPeople()) {
            allPeople.add(person);
        }
        // Remove people from allPeople who are currently in a team
        for (final Team team : project.getTeams()) {
            for (final Person person : team.getTeamMembers()) {
                allPeople.remove(person);
            }
        }
>>>>>>> branch 'feature/team' of ssh://git@eng-git.canterbury.ac.nz/seng302-2015/project-4.git

        // So we are left with ArrayList<Person> allPeople which contains only people who aren't in a team
        sourcePeople.setAll(allPeople);

        peopleListSelectionView.getSourceListView().setItems(sourcePeople);
        peopleListSelectionView.getTargetListView().setItems(targetPeople);
    }


    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    /**
     * Sets the team to be edited and populates fie lds if applicable
     * @param team Team to be edited
     */
    public void setTeam(Team team) {
        this.team = team;

        if (team == null) {
            // Then we are creating a new team
            stage.setTitle("Create Team");
            okButton.setText("Create Team");
        } else {
            // We are editing an existing team
            stage.setTitle("Edit Team");
            okButton.setText("Save");

            // Populate fields with existing data
            shortNameTextField.setText(team.getShortName());
            descriptionTextField.setText(team.getDescription());
            targetPeople.addAll(team.getTeamMembers());
        }

    }

    public void setProject(Project project) {
        this.project = project;
        populatePeopleListView();
    }
}
