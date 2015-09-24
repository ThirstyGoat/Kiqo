package com.thirstygoat.kiqo.gui.sprint;

import com.thirstygoat.kiqo.gui.DelayedValidationVisualizer;
import com.thirstygoat.kiqo.gui.FormButtonHandler;
import com.thirstygoat.kiqo.gui.nodes.GoatFilteredListSelectionView;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.util.*;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.utils.validation.visualization.ControlsFxVisualizer;
import de.saxsys.mvvmfx.utils.validation.visualization.ValidationVisualizer;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
* Created by Carina Blair on 3/08/2015.
*/
public class SprintFormView implements FxmlView<SprintViewModel>, Initializable {

    private FormButtonHandler formButtonHandler;
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField goalTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField releaseTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField teamTextField;
    @FXML
    private TextField backlogTextField;
    @FXML
    private GoatFilteredListSelectionView<Story> storySelectionView;
    @FXML
    private Button okButton;
    @FXML
    private Button prevButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Hyperlink whyHyperlink;
    @FXML
    private Label heading;
    @FXML
    private VBox detailsVBox;
    @FXML
    private VBox storiesVBox;

    @InjectViewModel
    private SprintViewModel viewModel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nameTextField.textProperty().bindBidirectional(viewModel.longNameProperty());
        goalTextField.textProperty().bindBidirectional(viewModel.goalProperty());
        startDatePicker.valueProperty().bindBidirectional(viewModel.startDateProperty());
        endDatePicker.valueProperty().bindBidirectional(viewModel.endDateProperty());
        releaseTextField.textProperty().bindBidirectional(viewModel.releaseProperty(),
                StringConverters.releaseStringConverter(viewModel.organisationProperty()));
        descriptionTextField.textProperty().bindBidirectional(viewModel.descriptionProperty());
        teamTextField.textProperty().bindBidirectional(viewModel.teamProperty(),
                StringConverters.teamStringConverter(viewModel.organisationProperty()));
        backlogTextField.textProperty().bindBidirectional(viewModel.backlogProperty(),
                StringConverters.backlogStringConverter(viewModel.organisationProperty()));

        Label headingLabel = new Label("Stories in Sprint");
        headingLabel.getStyleClass().add("form-field-label");
        storySelectionView.setHeader(headingLabel);
        storySelectionView.sourceItemsProperty().bindBidirectional(viewModel.eligableStories());
        storySelectionView.targetItemsProperty().bindBidirectional(viewModel.stories());
        storySelectionView.setStringPropertyCallback(story -> story.shortNameProperty());

        okButton.disableProperty().bind(viewModel.allValidation().validProperty().not());
        
        Utilities.initShortNameLengthLimiter(goalTextField.textProperty());
        FxUtils.setTextFieldSuggester(backlogTextField, viewModel.backlogsSupplier());
        FxUtils.setTextFieldSuggester(teamTextField, viewModel.teamsSupplier());
        FxUtils.setTextFieldSuggester(releaseTextField, viewModel.releasesSupplier());

        startDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (viewModel.releaseProperty().get() != null) {
                    if (endDatePicker.getValue() != null) {
                        if (item.isAfter(endDatePicker.getValue().minusDays(1))) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc0cb;");
                        }
                    }
                    if (item.isAfter(viewModel.releaseProperty().get().getDate())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            }
        });

        endDatePicker.setDayCellFactory(param -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (viewModel.releaseProperty().get() != null) {
                    if (item.isAfter(viewModel.releaseProperty().get().getDate())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
                if (startDatePicker.getValue() != null) {
                    if (item.isBefore(startDatePicker.getValue().plusDays(1))) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            }
        });

        Platform.runLater(() -> {
            goalTextField.requestFocus();
            // do this in here to ensure textFields definitely exist
            attachValidators();
        });

        setWhyHyperLink();
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

    private void setWhyHyperLink() {
        Label label = new Label();
        label.setText("Only stories that are marked as ready can be added to a sprint.");
        label.setPadding(new Insets(10, 10, 10, 10));
        PopOver readyWhyPopOver = new PopOver(label);
        readyWhyPopOver.setDetachable(false);

        whyHyperlink.setOnAction((e) -> {
            readyWhyPopOver.show(whyHyperlink);
        });
        whyHyperlink.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                readyWhyPopOver.hide(Duration.millis(0));
            }
        });
    }

    private void attachValidators() {
        DelayedValidationVisualizer validationVisualizer = new DelayedValidationVisualizer(new SimpleBooleanProperty(true));
        validationVisualizer.initVisualization(viewModel.goalValidation(), goalTextField, true);
        validationVisualizer.initVisualization(viewModel.releaseValidation(), releaseTextField, true);
        validationVisualizer.initVisualization(viewModel.startDateValidation(), startDatePicker, true);
        validationVisualizer.initVisualization(viewModel.endDateValidation(), endDatePicker, true);
        validationVisualizer.initVisualization(viewModel.teamValidation(), teamTextField, true);
        validationVisualizer.initVisualization(viewModel.backlogValidation(), backlogTextField, true);
        validationVisualizer.initVisualization(viewModel.longNameValidation(), nameTextField, true);
        validationVisualizer.initVisualization(viewModel.storiesValidation(), storySelectionView.getControl(), true);
    }

    public void setExitStrategy(Runnable exitStrategy) {
        formButtonHandler = new FormButtonHandler(viewModel::createCommand, exitStrategy);
    }

    public void okAction() {
        if (formButtonHandler != null) {
            formButtonHandler.okAction();
        }
    }

    public void cancelAction() {
        if (formButtonHandler != null) {
            formButtonHandler.cancelAction();
        }
    }

    public StringProperty headingTextProperty() {
        return heading.textProperty();
    }
}
