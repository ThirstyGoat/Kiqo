package seng302.group4.viewModel;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import seng302.group4.Allocation;
import seng302.group4.Organisation;
import seng302.group4.Project;
import seng302.group4.Team;
import seng302.group4.undo.Command;
import seng302.group4.undo.CompoundCommand;
import seng302.group4.undo.CreateAllocationCommand;
import seng302.group4.undo.EditCommand;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by Amy on 23/04/15.
 */
public class AllocationFormController implements Initializable {
    // UI
    private final PopOver errorPopOver = new PopOver();
    private Stage stage;
    // Command
    private boolean valid;
    private Command<?> command;
    // Model
    private Organisation organisation;
    private Project project;
    // Form Data
    private Team team = null;

    // FXML
    @FXML
    private TextField teamTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    private Allocation allocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startDatePicker.setPromptText("dd/mm/yyyy");
        endDatePicker.setPromptText("dd/mm/yyyy");
        setButtonHandlers();
        setTextFieldAutoCompletionBinding();

        Platform.runLater(teamTextField::requestFocus);

        datePickerChecker();
    }

    private void datePickerChecker() {
        startDatePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (endDatePicker.getValue() == null) {
                endDatePicker.setValue(startDatePicker.getValue().plusDays(1));
            }
        });
    }

    private AutoCompletionBinding<Team> setTextFieldAutoCompletionBinding() {
        // uses a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
        // uses a String converter so that the Team's short name is used.
        final AutoCompletionBinding<Team> binding = TextFields.bindAutoCompletion(teamTextField,
                new Callback<AutoCompletionBinding.ISuggestionRequest, Collection<Team>>() {
            @Override
            public Collection<Team> call(AutoCompletionBinding.ISuggestionRequest request) {
                final Collection<Team> teams = organisation.getTeams().stream()
                        .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                        .collect(Collectors.toList());
                return teams;
            }

        }, new StringConverter<Team>() {
            @Override
            public Team fromString(String string) {
                for (final Team team : organisation.getTeams()) {
                    if (team.getShortName().equals(string)) {
                        return team;
                    }
                }
                return null;
            }

            @Override
            public String toString(Team team) {
                return team.getShortName();
            }
        });

        teamTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // forces suggestion list to show
                binding.setUserInput("");
            }
        });

        return binding;
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                errorPopOver.hide(Duration.millis(0));
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> {
            errorPopOver.hide(Duration.millis(0));
            stage.close();
        });
    }

    private boolean validate() {
        // check that team exists (and keep a reference to the matching team)
        team = null;
        for (final Team p : organisation.getTeams()) {
            if (teamTextField.getText().equals(p.getShortName())) {
                team = p;
                break;
            }
        }
        if (team == null) {
            errorPopOver.setContentNode(new Label("Team \"" + teamTextField.getText() + "\" does not exist"));
            errorPopOver.show(teamTextField);
            return false;
        }

        if (startDatePicker.getValue() == null) {
            errorPopOver.setContentNode(new Label("Valid start date required"));
            errorPopOver.show(startDatePicker);
            return false;
        }

        if (endDatePicker.getValue() != null && !endDatePicker.getValue().isAfter(startDatePicker.getValue())) {
            errorPopOver.setContentNode(new Label("End date must follow start date"));
            errorPopOver.show(endDatePicker);
            return false;
        }

        boolean dateRangesOverlap = false;
        for (final Allocation a : team.getAllocations()) {
            // If the end dates are null, then the allocation has no specified period
            // to make things easier, we pretend that they're infinite, ie. LocalDate.MAX
            final LocalDate aEnd = (a.getEndDate() == null) ? LocalDate.MAX : a.getEndDate();
            final LocalDate bEnd = (endDatePicker.getValue() == null) ? LocalDate.MAX : endDatePicker.getValue();
            if ((a.getStartDate().isBefore(bEnd)) && (aEnd.isAfter(startDatePicker.getValue()))) {
                dateRangesOverlap = true;
                break;
            }
        }

        if (dateRangesOverlap) {
            errorPopOver.setContentNode(new Label("Team is already allocated to a project during this period!"));
            errorPopOver.show(startDatePicker);
            return false;
        }

        valid = true;
        setCommand();
        return true;
    }

    private void setCommand() {
        if (allocation == null) {
            allocation = new Allocation(team, startDatePicker.getValue(), endDatePicker.getValue(), project);
            command = new CreateAllocationCommand(allocation);
        } else {
            // edit command
            final ArrayList<Command<?>> changes = new ArrayList<>();
            if (!team.equals(allocation.getTeam())) {
                changes.add(new EditCommand<>(allocation, "team", team));
            }
            if (!startDatePicker.getValue().equals(allocation.getStartDate())) {
                changes.add(new EditCommand<>(allocation, "startDate", startDatePicker.getValue()));
            }
            if (!endDatePicker.getValue().equals(allocation.getEndDate())) {
                changes.add(new EditCommand<>(allocation, "endDate", endDatePicker.getValue()));
            }
            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Allocation", changes);
        }
    }

    public boolean isValid() {
        return valid;
    }

    public Command<?> getCommand() {
        return command;
    }

    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
        if (project == null) {
            throw new RuntimeException("Project must not be null for Allocation dialog");
        }
        if (allocation == null) {
            // We are creating a new allocation (for an existing project)
            stage.setTitle("Create Allocation");
            okButton.setText("Create Allocation");
        } else {
            // edit an existing allocation
            stage.setTitle("Edit Allocation");
            okButton.setText("Save");

            teamTextField.setText(allocation.getTeam().getShortName());
            startDatePicker.setValue(allocation.getStartDate());
            endDatePicker.setValue(allocation.getEndDate());
        }
    }

    /**
     * Sets the project which will have teams allocated to it
     *
     * @param project destination Project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}
