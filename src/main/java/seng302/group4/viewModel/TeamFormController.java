package seng302.group4.viewModel;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import org.controlsfx.control.PopOver;

import javafx.util.Duration;
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
    private Person scrumMaster;
    private Person productOwner;
    private ArrayList<Person> devTeam = new ArrayList<>();

    private final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();
    private final ObservableList<Person> targetPeople = FXCollections.observableArrayList();
    private final PopOver errorPopOver = new PopOver();

    private ArrayList<RadioButton> poRadioButtons = new ArrayList<>();
    private ArrayList<RadioButton> smRadioButtons = new ArrayList<>();

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
            command = new CreateTeamCommand(shortNameTextField.getText(), descriptionTextField.getText(), teamMembers,
                    productOwner, scrumMaster, devTeam);
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
        setTargetPeopleCellFactory(peopleListSelectionView.getTargetListView());

        // Set change listener on target list view
        targetPeople.addListener(new ListChangeListener<Person>() {
            @Override
            public void onChanged(Change<? extends Person> c) {
                c.next();
                for (Person person : c.getRemoved()) {
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
            }
        });


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

    private void setTargetPeopleCellFactory(ListView<Person> listView) {
        listView.setCellFactory(view -> {
            final ListCell<Person> cell = new ListCell<Person>() {
                @Override
                public void updateItem(Person person, boolean empty) {
                    super.updateItem(person, empty);
                    if (person != null) {

                        BorderPane borderPane = new BorderPane();
                        HBox hbox = new HBox();
                        borderPane.setRight(hbox);
                        Label label = new Label(person.getShortName());
                        borderPane.setLeft(label);

                        ToggleGroup radioGroup = new ToggleGroup();
                        RadioButton radioPo = new RadioButton();
                        RadioButton radioSm = new RadioButton();
                        RadioButton radioDev = new RadioButton();
                        RadioButton radioOther = new RadioButton();
                        radioPo.setToggleGroup(radioGroup);
                        radioSm.setToggleGroup(radioGroup);
                        radioDev.setToggleGroup(radioGroup);
                        radioOther.setToggleGroup(radioGroup);

                        // Hide PO/SM Radio Buttons if the person doesn't have the skill
                        if (!person.getSkills().contains(project.getPoSkill())) {
                            radioPo.setDisable(true);
                        }
                        if (!person.getSkills().contains(project.getSmSkill())) {
                            radioSm.setDisable(true);
                        }

                        // Select appropriate RadioButton
                        if (person == productOwner) {
                            radioPo.setSelected(true);
                        } else if (person == scrumMaster) {
                            radioSm.setSelected(true);
                        } else if (devTeam != null && devTeam.contains(person)) {
                            radioDev.setSelected(true);
                        } else {
                            radioOther.setSelected(true);
                        }

//                        // Set colors
//                        radioSm.setStyle("-fx-mark-color: blue");

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
            };
            return cell;
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
        final ObservableList<Person> sourcePeople = FXCollections.observableArrayList();

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

            productOwner = team.getProductOwner();
            scrumMaster = team.getScrumMaster();
            devTeam.addAll(team.getDevTeam().stream().collect(Collectors.toList()));
        }

    }

    public void setProject(Project project) {
        this.project = project;
        populatePeopleListView();
    }
}
