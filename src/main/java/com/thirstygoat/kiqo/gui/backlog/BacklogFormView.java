package com.thirstygoat.kiqo.gui.backlog;

import com.thirstygoat.kiqo.gui.DelayedValidationVisualizer;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Scale;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.FxUtils;
import com.thirstygoat.kiqo.util.StringConverters;
import com.thirstygoat.kiqo.util.Utilities;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by lih18 on 20/05/15.
 */
public class BacklogFormView implements FxmlView<BacklogFormViewModel>, Initializable {

    @InjectViewModel
    BacklogFormViewModel viewModel;
    @FXML
    private TextField longNameTextField;
    @FXML
    private TextField shortNameTextField;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private TextField projectTextField;
    @FXML
    private TextField productOwnerTextField;
    @FXML
    private ComboBox<Scale> scaleComboBox;
    @FXML
    private GoatFilteredListSelectionView<Story> storySelectionView;
    @FXML
    private VBox detailsVBox;
    @FXML
    private VBox storiesVBox;
    @FXML
    private Label heading;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button prevButton;

    @Override
    public void initialize(final URL location, ResourceBundle resources) {
        longNameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        shortNameTextField.textProperty().bindBidirectional(viewModel.shortNameProperty());
        descriptionTextArea.textProperty().bindBidirectional(viewModel.descriptionProperty());
        projectTextField.textProperty().bindBidirectional(viewModel.projectProperty(),
                StringConverters.projectStringConverter(viewModel.organisationProperty()));
        productOwnerTextField.textProperty().bindBidirectional(viewModel.productOwnerProperty(),
                StringConverters.personStringConverter(viewModel.organisationProperty()));

        scaleComboBox.setItems(FXCollections.observableArrayList(Scale.values()));
        scaleComboBox.getSelectionModel().selectFirst(); // Selects Fibonacci as default

        storySelectionView.bindSelectedItems(viewModel.stories());
        storySelectionView.bindAllItems(viewModel.eligibleStories());
        storySelectionView.setStringPropertyCallback(Story::shortNameProperty);

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());

        Utilities.initShortNameSuggester(longNameTextField.textProperty(), shortNameTextField.textProperty());
        FxUtils.setTextFieldSuggester(projectTextField, viewModel.projectSupplier());
        FxUtils.setTextFieldSuggester(productOwnerTextField, viewModel.productOwnerSupplier());
        FxUtils.enableShiftEnter(descriptionTextArea, okButton::fire);

        Platform.runLater(() -> {
            longNameTextField.requestFocus();
            setPrompts();
            attachValidators();
        });

        setNextButton();
    }

    private void setNextButton() {
        EventHandler<ActionEvent> nextEventHandler = event -> {
            detailsVBox.setVisible(false);
            detailsVBox.setManaged(false);
            storiesVBox.setVisible(true);

            okButton.setText("Done");

            prevButton.setDisable(false);

            okButton.setOnAction(event1 -> okAction());
        };

        prevButton.setOnAction(event -> {
            detailsVBox.setVisible(true);
            detailsVBox.setManaged(true);
            storiesVBox.setVisible(false);
            storiesVBox.setManaged(false);

            prevButton.setDisable(true);

            okButton.setText("Add Stories");
            okButton.setOnAction(nextEventHandler);
        });

        okButton.setOnAction(nextEventHandler);
    }

    private void attachValidators() {
        DelayedValidationVisualizer validationVisualizer = new DelayedValidationVisualizer(viewModel.dirtyProperty());
        validationVisualizer.initVisualization(viewModel.longNameValidation(), longNameTextField, true);
        validationVisualizer.initVisualization(viewModel.shortNameValidation(), shortNameTextField, true);
        validationVisualizer.initVisualization(viewModel.descriptionValidation(), descriptionTextArea, false);
        validationVisualizer.initVisualization(viewModel.projectValidation(), projectTextField, true);
        validationVisualizer.initVisualization(viewModel.productOwnerValidation(), productOwnerTextField, true);
    }

    private void setPrompts() {
        longNameTextField.setPromptText("Paddock");
        shortNameTextField.setPromptText("Must be under " + Utilities.SHORT_NAME_MAX_LENGTH + " characters and unique");
        descriptionTextArea.setPromptText("Describe this backlog");
    }

    public void okAction() {
        viewModel.okAction();
    }

    public void cancelAction() {
        viewModel.cancelAction();
    }

    public StringProperty headingProperty() {
        return heading.textProperty();
    }
}
