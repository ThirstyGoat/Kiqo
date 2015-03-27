package seng302.group4.viewModel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Stage;
import seng302.group4.Person;
import seng302.group4.Project;
import seng302.group4.Team;
import seng302.group4.customNodes.GoatListSelectionView;
import seng302.group4.undo.CreateTeamCommand;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
    private CreateTeamCommand command;
    private boolean valid = false;

    private ObservableList<Person> targetPeople = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setListSelectionViewSettings();
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
    }

    private boolean validate() {
        // TODO Add validation for short name uniqueness

        setCommand();
        valid = true;
        return true;
    }

    private void setCommand() {
        ArrayList<Person> teamMembers = new ArrayList<>();
        teamMembers.addAll(targetPeople);
        command = new CreateTeamCommand(shortNameTextField.getText(), descriptionTextField.getText(), teamMembers);
    }

    public boolean isValid() {
        return valid;
    }

    public CreateTeamCommand getCommand() {
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
            ListCell<Person> cell = new ListCell<Person>() {
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
        ObservableList<Person> sourcePeople = FXCollections.observableArrayList();

        project.getPeople().stream().filter(person -> !sourcePeople.contains(person)).forEach(sourcePeople::add);

        peopleListSelectionView.getSourceListView().setItems(sourcePeople);
        peopleListSelectionView.getTargetListView().setItems(targetPeople);
    }


    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    /**
     * Sets the team to be edited and populates fields if applicable
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
