package com.thirstygoat.kiqo.viewModel.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.command.CompoundCommand;
import com.thirstygoat.kiqo.command.CreateAllocationCommand;
import com.thirstygoat.kiqo.command.EditCommand;
import com.thirstygoat.kiqo.model.Allocation;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Project;
import com.thirstygoat.kiqo.model.Team;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Amy on 23/04/15.
 */
public class AllocationFormController extends FormController<Allocation> {
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private boolean valid;
    private Command<?> command;
    private Organisation organisation;
    private Project project;
    private Team team = null;
    // Begin FXML Injections
    @FXML
    private TextField teamTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Label teamLabel;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    private Allocation allocation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setButtonHandlers();
        Platform.runLater(teamTextField::requestFocus);
    }

    private void setValidationSupport() {
        Predicate<String> projectValidation;
        if(team == null) {
            projectValidation = s -> {
                for (final Team t : organisation.getTeams()) {
                    if (t.getShortName().equals(teamTextField.getText())) {
                        team = t;
                        final LocalDate sd = startDatePicker.getValue();
                        final LocalDate ed = endDatePicker.getValue();
                        startDatePicker.setValue(null);
                        endDatePicker.setValue(null);
                        startDatePicker.setValue(sd);
                        endDatePicker.setValue(ed);
                        return true;
                    }
                }
                return false;
            };
            validationSupport.registerValidator(teamTextField, Validator.createPredicateValidator(projectValidation,
                    "Team must already exist"));
        } else {
            projectValidation = s -> {
                for (final Project t : organisation.getProjects()) {
                    if (t.getShortName().equals(teamTextField.getText())) {
                        project = t;
                        final LocalDate sd = startDatePicker.getValue();
                        final LocalDate ed = endDatePicker.getValue();
                        startDatePicker.setValue(null);
                        endDatePicker.setValue(null);
                        startDatePicker.setValue(sd);
                        endDatePicker.setValue(ed);
                        return true;
                    }
                }
                return false;
            };
            validationSupport.registerValidator(teamTextField, Validator.createPredicateValidator(projectValidation,
                    "Project must already exist"));
        }

        final Predicate<LocalDate> endDateOverlapValidatorPredicate = localDate -> {
            if (team == null) {
                return true;
            }
            boolean dateRangesOverlap = false;
            for (final Allocation a : team.getAllocations()) {
                // If the end dates are null, then the allocation has no specified period
                // to make things easier, we pretend that they're infinite, ie. LocalDate.MAX
                final LocalDate aEnd = (a.getEndDate() == null) ? LocalDate.MAX : a.getEndDate();
                final LocalDate bEnd = (endDatePicker.getValue() == null) ? LocalDate.MAX : endDatePicker.getValue();

                if(startDatePicker.getValue() != null) {
                    if ((a.getStartDate().isBefore(bEnd)) && (aEnd.isAfter(startDatePicker.getValue())) && !a.equals(allocation)) {
                        dateRangesOverlap = true;
                        break;
                    }
                }
            }

            return !dateRangesOverlap;
        };

        final Validator endDateOverlapValidator = Validator.createPredicateValidator(endDateOverlapValidatorPredicate,
                "This team is already allocated to a project during this period");


        final Predicate<LocalDate> startDateNullValidatorPredicate = localDate -> {
            final LocalDate edpv = endDatePicker.getValue();
            endDatePicker.setValue(LocalDate.MIN);
            endDatePicker.setValue(edpv);
            return startDatePicker.getValue() != null;
        };

        final Validator startDateNullValidator = Validator.createPredicateValidator(startDateNullValidatorPredicate,
                "Start date must not be empty");
        // Create a validator for startDate that combines two validators
        final Validator startDateValidator = Validator.combine(startDateNullValidator);

        validationSupport.registerValidator(startDatePicker, startDateValidator);

        final Predicate<LocalDate> endDateBeforeValidatorPredicate = new Predicate<LocalDate>() {
            @Override
            public boolean test(LocalDate localDate) {
                if (localDate == null) {
                    return true;
                } else if (startDatePicker.getValue() == null) {
                    return true;
                } else {
                    return localDate.isAfter(startDatePicker.getValue());
                }
            }
        };

        final Validator endDateBeforeValidator = Validator.createPredicateValidator(endDateBeforeValidatorPredicate,
                "End date must not come before the start date");

        final Validator endDateValidator = Validator.combine(endDateBeforeValidator, endDateOverlapValidator);

        validationSupport.registerValidator(endDatePicker, endDateValidator);

        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Then invalid, disable ok button
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        });
    }

    private void setPrompts() {
        if (team == null) {
            teamTextField.setPromptText("Team this allocation is associated with");
        } else {
            teamTextField.setPromptText("Project this allocation is associated with");
        }
        startDatePicker.setPromptText("dd/mm/yyyy");
        endDatePicker.setPromptText("dd/mm/yyyy");
        endDatePicker.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd/MM/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

            {
                endDatePicker.setPromptText(pattern.toLowerCase());
            }

            @Override
            public String toString(LocalDate date) {
                if (date != null && !date.equals(LocalDate.MAX)) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return LocalDate.MAX;
                }
            }
        });
    }

    private AutoCompletionBinding<Team> setTextFieldAutoCompletionBindingTeam() {
        // uses a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
        // uses a String converter so that the Team's short name is used.
        final AutoCompletionBinding<Team> binding = TextFields.bindAutoCompletion(teamTextField,
                request -> {
                    return organisation.getTeams().stream()
                            .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                            .collect(Collectors.toList());
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

    private AutoCompletionBinding<Project> setTextFieldAutoCompletionBindingProject() {
            // uses a callback to get an up-to-date project list, instead of just whatever exists at initialisation.
            // uses a String converter so that the Team's short name is used.
            final AutoCompletionBinding<Project> binding = TextFields.bindAutoCompletion(teamTextField,
                    request -> {
                        return organisation.getProjects().stream()
                                .filter(t -> t.getShortName().toLowerCase().contains(request.getUserText().toLowerCase()))
                                .collect(Collectors.toList());
                    }, new StringConverter<Project>() {
                        @Override
                        public Project fromString(String string) {
                            for (final Project project : organisation.getProjects()) {
                                if (project.getShortName().equals(string)) {
                                    return project;
                                }
                            }
                            return null;
                        }

                        @Override
                        public String toString(Project project) {
                            return project.getShortName();
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
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
    }

    /**
     * Performs validation checks and displays error popovers where applicable
     * @return all fields are valid
     */
    private boolean validate() {
        if (validationSupport.isInvalid()) {
            return false;
        } else {
            valid = true;
        }
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
            if (endDatePicker.getValue() == null && !allocation.getEndDate().equals(LocalDate.MAX)) {
                changes.add(new EditCommand<>(allocation, "endDate", LocalDate.MAX));
            } else if (!endDatePicker.getValue().equals(allocation.getEndDate())) {
                changes.add(new EditCommand<>(allocation, "endDate", endDatePicker.getValue()));
            }
            valid = !changes.isEmpty();
            command = new CompoundCommand("Edit Allocation", changes);
        }
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command<?> getCommand() {
        return command;
    }

    @Override
    public void setStage(Stage stage)  {
        this.stage = stage;
    }

    @Override
    public void populateFields(Allocation allocation) throws RuntimeException {
        this.allocation = allocation;


        if (allocation == null) {
            // We are creating a new allocation (for an existing project)
            stage.setTitle("Create Allocation");
            okButton.setText("Create Allocation");
        } else {
            // edit an existing allocation
            stage.setTitle("Edit Allocation");
            okButton.setText("Save");

            if (project != null) {
                teamTextField.setText(allocation.getTeam().getShortName());
            }

            startDatePicker.setValue(allocation.getStartDate());
            endDatePicker.setValue(allocation.getEndDate());
        }

        if (project == null) {
            teamLabel.setText("Project:");
            setTextFieldAutoCompletionBindingProject();
        } else if (team == null) {
            teamLabel.setText("Team:");
            setTextFieldAutoCompletionBindingTeam();
        }

        setPrompts();
        setValidationSupport();

    }

    /**
     * Sets the project which will have teams allocated to it
     *
     * @param project destination Project
     */
    public void setProject(Project project) {
        this.project = project;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }
}