package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.command.Command;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lih18 on 20/05/15.
 */
public class BacklogFormView implements FxmlView<BacklogFormViewModel>, Initializable {
    private final int SHORT_NAME_SUGGESTED_LENGTH = 20;
    private final int SHORT_NAME_MAX_LENGTH = 20;
    private final ValidationSupport validationSupport = new ValidationSupport();
    private Stage stage;
    private boolean valid = false;
    private BooleanProperty shortNameModified = new SimpleBooleanProperty(false);
    private Command command;
    
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
    private GoatFilteredListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    @InjectViewModel
    BacklogFormViewModel viewModel;

    @Override
    public void initialize(final URL location, ResourceBundle resources) {
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectProperty(),
                StringConverters.projectStringConverter(viewModel.organisationProperty()));
        productOwnerTextField.textProperty().bindBidirectional(viewModel.productOwnerProperty(),
                StringConverters.personStringConverter(viewModel.organisationProperty()));

        scaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        scaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default

        storySelectionView.setHeader(new Label("Stories in Project:"));
        storySelectionView.targetItemsProperty().bindBidirectional(viewModel.stories());
        storySelectionView.sourceItemsProperty().bind(viewModel.eligableStories());

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());

        FxUtils.setTextFieldSuggester(projectTextField, viewModel.projectSupplier());
        FxUtils.setTextFieldSuggester(productOwnerTextField, viewModel.productOwnerSupplier());

        Platform.runLater(() -> {
            longNameTextField.requestFocus();
            attachValidators();
        });
    }

    private void attachValidators() {
        ValidationVisualizer validationVisualizer = new ControlsFxVisualizer();
        validationVisualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        validationVisualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextField, false);
        validationVisualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
        validationVisualizer.initVisualization(viewModel.productOwnerValidation(), productOwnerTextField, true);
    }

    private void setPrompts() {
        longNameTextField.setPromptText("Paddock");
        shortNameTextField.setPromptText("Must be under 20 characters and unique.");
        descriptionTextField.setPromptText("Describe this backlog");
    }

    public void okAction() {
        viewModel.okAction();
    }

    public void cancelAction() {
        viewModel.cancelAction();
    }
}
