package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;

import seng302.group4.Organisation;
import seng302.group4.Person;
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

    private final ArrayList<Person> devTeam = new ArrayList<>();
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
    private final ObservableList<Person> targetPeople = FXCollections.observableArrayList();
    private final PopOver errorPopOver = new PopOver();
    private final ArrayList<RadioButton> poRadioButtons = new ArrayList<>();
    private final ArrayList<RadioButton> smRadioButtons = new ArrayList<>();
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
    private Organisation organisation;
    private Team team;
    private Command<?> command;
    private boolean valid = false;
    private Person scrumMaster;
    private Person productOwner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        setTextFieldListener();
        setShortNameLengthRestrictor();

        Platform.runLater(shortNameTextField::requestFocus);
    }

    /**
     * Sets up a listener on the name field of team to restrict it to the predefined maximum length
     */
    private void setShortNameLengthRestrictor() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
                errorPopOver.setContentNode(new Label("Short name must be under " + SHORT_NAME_MAX_LENGTH +
                        " characters"));
                errorPopOver.show(shortNameTextField);
            }
        });
    }

    private void setTextFieldListener() {
        shortNameTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // then focus is on this text field
                errorPopOver.hide();
            }
            else {
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
            errorPopOver.hide(Duration.millis(0));
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
        for (final Team t : organisation.getTeams()) {
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
            command = new CreateTeamCommand(shortNameTextField.getText(), descriptionTextField.getText(), teamMembers,
                    productOwner, scrumMaster, devTeam, organisation);
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

            if (productOwner != team.getProductOwner()) {
                changes.add(new EditCommand<>(team, "productOwner", productOwner));
            }

            if (scrumMaster != team.getScrumMaster()) {
                changes.add(new EditCommand<>(team, "scrumMaster", scrumMaster));
            }

            if (!(devTeam.containsAll(team.getDevTeam()) && team.getDevTeam().containsAll(devTeam))) {
                changes.add(new EditCommand<>(team, "devTeam", devTeam));
            }

            // We need to find out who left the team and set their team to null
            // We also need to find out who entered the team, and set their team to this team
            // Old Team = team.getTeamMembers();
            // New Team = teamMembers
            // New Members = New Team - Old Team
            // Old Members = Old Team - New Team

            final ArrayList<Person> newMembers = new ArrayList<>(teamMembers);
            newMembers.removeAll(team.getTeamMembers());
            final ArrayList<Person> oldMembers = new ArrayList<>(team.getTeamMembers());
            oldMembers.removeAll(teamMembers);

            // Loop through all the new members and add a command to set their team
            // Set the person's team field to this team
            changes.addAll(newMembers.stream().map(person -> new EditCommand<>(person, "team", team))
                    .collect(Collectors.toList()));

            // Loop through all the old members and add a command to remove their team
            // Set the person's team field to null, since they're no longer in the team
            changes.addAll(oldMembers.stream().map(person -> new EditCommand<>(person, "team", null))
                    .collect(Collectors.toList()));

            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Team", changes);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public Command<?> getCommand() {
        return command;
    }

    public void setListSelectionViewSettings() {
        peopleListSelectionView.setSourceHeader(new Label("People Available:"));
        final BorderPane targetHeader = new BorderPane();
        targetHeader.setLeft(new Label("People Selected:"));

        peopleListSelectionView.setPadding(new Insets(0, 0, 0, 0));

        final Text poText = new Text(organisation.getPoSkill().getShortName().substring(0, 2));
        poText.setFill(Color.BLUE);
        final Text smText = new Text(organisation.getSmSkill().getShortName().substring(0, 2));
        smText.setFill(Color.RED);
        final Text devText = new Text("Dev");
        devText.setFill(Color.GREEN);
        final Text otherText = new Text("Other");
        final TextFlow legend = new TextFlow(poText, smText, devText, otherText);

        targetHeader.setRight(legend);
        peopleListSelectionView.setTargetHeader(targetHeader);

        // Set the custom cell factory for the skills lists
        // Thank GoatListSelectionView for this fabulous method

        setCellFactory(peopleListSelectionView.getSourceListView());
        setTargetPeopleCellFactory(peopleListSelectionView.getTargetListView());

        // Set change listener on target list view
        targetPeople.addListener((ListChangeListener<Person>) c -> {
            c.next();
            for (final Person person : c.getRemoved()) {
                // Remove person from role of PO/SM/DevTeam if applicable
                if (productOwner == person) {
                    productOwner = null;
                    System.out.println("removing " + person + " from po role");
                } else if (scrumMaster == person) {
                    scrumMaster = null;
                    System.out.println("removing " + person + " from sm role");
                } else if (devTeam.contains(person)) {
                    devTeam.remove(person);
                    System.out.println("removing " + person + " from devteam role");
                }
            }
        });


    }

    private void setCellFactory(ListView<Person> listView) {
        listView.setCellFactory(view -> new ListCell<Person>() {
            @Override
            public void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (person != null) {
                    setText(person.getShortName());
                } else {
                    setText(null);
                }
            }
        });
    }

    private void setTargetPeopleCellFactory(ListView<Person> listView) {
        listView.setCellFactory(view -> new ListCell<Person>() {
            @Override
            public void updateItem(Person person, boolean empty) {
                super.updateItem(person, empty);
                if (person != null) {

                    final BorderPane borderPane = new BorderPane();
                    final HBox hbox = new HBox();
                    borderPane.setRight(hbox);
                    final Label label = new Label(person.getShortName());
                    borderPane.setLeft(label);

                    final ToggleGroup radioGroup = new ToggleGroup();
                    final RadioButton radioPo = new RadioButton();
                    final RadioButton radioSm = new RadioButton();
                    final RadioButton radioDev = new RadioButton();
                    final RadioButton radioOther = new RadioButton();
                    radioPo.setToggleGroup(radioGroup);
                    radioSm.setToggleGroup(radioGroup);
                    radioDev.setToggleGroup(radioGroup);
                    radioOther.setToggleGroup(radioGroup);
                    radioPo.setStyle("-fx-mark-color: blue;");
                    radioSm.setStyle("-fx-mark-color: red;");
                    radioDev.setStyle("-fx-mark-color: green;");

                    // Disable PO/SM Radio Buttons if the person doesn't have
                    // the skill
                    if (!person.getSkills().contains(organisation.getPoSkill())) {
                        radioPo.setDisable(true);
                    }
                    if (!person.getSkills().contains(organisation.getSmSkill())) {
                        radioSm.setDisable(true);
                    }

                    // Select appropriate RadioButton
                    if (productOwner == person || (team != null && team.getProductOwner() == person)) {
                        radioPo.setSelected(true);
                    } else if (scrumMaster == person || (team != null && team.getScrumMaster() == person)) {
                        radioSm.setSelected(true);
                    } else if ((devTeam != null && devTeam.contains(person)) ||
                            (team != null && team.getDevTeam() != null && team.getDevTeam().contains(person))) {
                        radioDev.setSelected(true);
                    } else {
                        radioOther.setSelected(true);
                    }

                    hbox.getChildren().addAll(radioPo, radioSm, radioDev, radioOther);

                    setupRadioPoListener(radioPo, radioOther, person);
                    setupRadioSmListener(radioSm, radioOther, person);
                    setupRadioDevListener(radioDev, person);

                    setGraphic(borderPane);

                } else {
                    setGraphic(null);
                    setText(null);
                }
            }
        });
    }

    private void setupRadioDevListener(RadioButton radioDev, Person person) {
        radioDev.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                devTeam.add(person);
                System.out.println("Assigned " + person.getShortName() + " to Dev Role");
            } else {
                devTeam.remove(person);
                System.out.println("Removed " + person.getShortName() + " from Dev Role");

            }
        });
    }

    private void setupRadioSmListener(RadioButton radioSm, RadioButton radioOther, Person person) {
        smRadioButtons.add(radioSm);

        radioSm.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If selected
                scrumMaster = person;
                smRadioButtons.stream().filter(rb -> rb != radioSm).forEach(rb -> rb.setSelected(false));
                System.out.println("Assigned " + person.getShortName() + " to SM Role");
            } else {
                if (radioSm.getToggleGroup().getSelectedToggle() == null) {
                    radioOther.setSelected(true);
                }
                if (scrumMaster == person) {
                    scrumMaster = null;
                    System.out.println("Removed " + person.getShortName() + " from SM Role");
                }
            }
        });
    }

    private void setupRadioPoListener(RadioButton radioPo, RadioButton radioOther, Person person) {
        poRadioButtons.add(radioPo);

        radioPo.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // If selected
                productOwner = person;
                poRadioButtons.stream().filter(rb -> rb != radioPo).forEach(rb -> rb.setSelected(false));
                System.out.println("Assigned " + person.getShortName() + " to PO Role");
            } else {
                if (radioPo.getToggleGroup().getSelectedToggle() == null) {
                    radioOther.setSelected(true);
                }
                if (productOwner == person) {
                    productOwner = null;
                    System.out.println("Removed " + person.getShortName() + " from PO Role");
                }
            }
        });
    }

    private void populatePeopleListView() {
        // all people observableList = project.getPeople();
        final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
        sourcePeople.addAll(organisation.getPeople());

        // Remove all people from sourcePeople that are currently in a team
        organisation.getPeople().stream().filter(person -> person.getTeam() != null).forEach(sourcePeople::remove);

        organisation.getPeople().addListener((ListChangeListener<Person>) c -> {
            c.next();
            // We remove people from the sourcePeople that were removed from the project.
            // Note that this shouldn't actually be possible since undo/redo should be disabled
            sourcePeople.removeAll(c.getRemoved());
            targetPeople.removeAll(c.getRemoved());
            for (final Person person : c.getAddedSubList()) {
                if (person.getTeam() == team) {
                    targetPeople.add(person);
                } else {
                    sourcePeople.add(person);
                }
            }
        });

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

            productOwner = team.getProductOwner();
            scrumMaster = team.getScrumMaster();
            devTeam.addAll(team.getDevTeam().stream().collect(Collectors.toList()));
        }

    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
        populatePeopleListView();
    }
}
