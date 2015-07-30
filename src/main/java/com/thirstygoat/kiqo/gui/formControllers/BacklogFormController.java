package com.thirstygoat.kiqo.gui.formControllers;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.nodes.GoatDialog;
import com.thirstygoat.kiqo.gui.nodes.GoatListSelectionView;
import com.thirstygoat.kiqo.gui.viewModel.BacklogFormViewModel;
import com.thirstygoat.kiqo.model.Backlog;
import com.thirstygoat.kiqo.model.Organisation;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.Utilities;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Created by lih18 on 20/05/15.
 */
public class BacklogFormController extends FormController<Backlog> {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private BacklogFormViewModel viewModel;
    private Stage stage;
    private boolean valid = false;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private Command<?> command;
    
    // Begin FXML Injections
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField productOwnerTextField;
    @FXML
    private ComboBox<Scale> scaleComboBox;
    @FXML
    private GoatListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @Override
    public void initialize(final URL location, ResourceBundle resources) {
        viewModel = new BacklogFormViewModel();
        initialiseScaleCombobox();
        setShortNameHandler();
        setPrompts();
        setButtonHandlers();
        Utilities.setNameSuggester(longNameTextField, shortNameTextField, SHORT_NAME_SUGGESTED_LENGTH,
                shortNameModified);
        Platform.runLater(longNameTextField::requestFocus);
    }

    private void initialiseScaleCombobox() {
        scaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        scaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default
    }

    private void bindFields() {
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectNameProperty());
        productOwnerTextField.textProperty().bindBidirectional(viewModel.productOwnerNameProperty());
        scaleComboBox.valueProperty().bindBidirectional(viewModel.scaleProperty());
        storySelectionView.getTargetListView().itemsProperty().bindBidirectional(viewModel.targetStoriesProperty());
        storySelectionView.getSourceListView().itemsProperty().bindBidirectional(viewModel.sourceStoriesProperty());
    }

//    private void setListeners() {
//        project.addListener(((observable, oldValue, newValue) -> {
//            storySelectionView.getTargetListView().getItems().clear();
//            setStoryListSelectionViewData();
//        }));
//    }

    private void setValidationSupport() {
        validationSupport.registerValidator(longNameTextField,
                Validator.createPredicateValidator(viewModel.getLongNameValidation(), "Long name must not be empty"));

        validationSupport.registerValidator(shortNameTextField, 
                Validator.createPredicateValidator(viewModel.getShortNameValidation(), "Short name must be unique and not empty"));

        validationSupport.registerValidator(projectTextField, 
                Validator.createPredicateValidator(viewModel.getProjectValidation(), "Project must already exist"));

        validationSupport.registerValidator(productOwnerTextField, 
                Validator.createPredicateValidator(viewModel.getProductOwnerValidation(), "Person must already exist and have the PO skill"));

        validationSupport.registerValidator(scaleComboBox,
                Validator.createPredicateValidator(viewModel.getScaleValidation(), "Estimation Scale must not be empty"));
        
        validationSupport.invalidProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue);
        });
    }

    private void setPrompts() {
        longNameTextField.setPromptText("Paddock");
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        descriptionTextField.setPromptText("Describe this backlog");
    }

    private void setupStoriesList() {
        storySelectionView.setSourceHeader(new Label("Stories Available:"));
        storySelectionView.setTargetHeader(new Label("Stories in Backlog"));

        storySelectionView.setPadding(new Insets(0, 0, 0, 0));

        storySelectionView.setCellFactories(view -> {
            final ListCell<Story> cell = new ListCell<Story>() {
                @Override
                public void updateItem(Story item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(item != null ? item.getShortName() : null);
                }
            };
            return cell;
        });
    }


    @Override
    public void populateFields(final Backlog backlog) {
        viewModel.setBacklog(backlog);
        okButton.setText("Done");
        bindFields();
    }

    private void setButtonHandlers() {
        okButton.setOnAction(event -> {
            if (validate()) {
                stage.close();
            }
        });

        cancelButton.setOnAction(event -> stage.close());
    }

    private void setCommand() {
        viewModel.setCommand();
    }

    private boolean validate() {
        List<Story> conflicts = viewModel.getConflictingScales();
        if (validationSupport.isInvalid()) {
            return false;
        } else if (!conflicts.isEmpty()) {
            final String[] options = {"Okay", "Cancel"};
            final String query = "The following stories will have their estimate reset due to their scales not matching the backlogs." +
                    "\n\nStories: " + Utilities.concatenateItemsList(conflicts, 5);

            final String result = GoatDialog.createBasicButtonDialog(stage, "Are you sure?", "Conflicting scales", query, options);

            if (result.equals("Okay")) {
                valid = true;
            } else {
                return false;
            }
        } else {
            valid = true;
        }
        setCommand();
        return true;
    }

    private void setShortNameHandler() {
        shortNameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Auto populate short name text field
            if (!Objects.equals(newValue, longNameTextField.getText().substring(0,
                    Math.min(longNameTextField.getText().length(), SHORT_NAME_SUGGESTED_LENGTH)))) {
                shortNameModified.set(true);
            }

            // Restrict length of short name text field
            if (shortNameTextField.getText().length() > SHORT_NAME_MAX_LENGTH) {
                shortNameTextField.setText(shortNameTextField.getText().substring(0, SHORT_NAME_MAX_LENGTH));
            }
        });
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public Command<?> getCommand() {
        return viewModel.getCommand() ;
    }

    @Override
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setOrganisation(Organisation organisation) {
        viewModel.setOrganisation(organisation);
        setupStoriesList();
        setTextFieldSuggester(projectTextField, organisation.getProjects());
        setTextFieldSuggester(productOwnerTextField, organisation.getEligiblePOs());
        setValidationSupport();
    }
}
