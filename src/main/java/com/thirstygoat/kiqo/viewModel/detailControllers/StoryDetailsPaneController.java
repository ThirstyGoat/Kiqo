package com.thirstygoat.kiqo.viewModel.detailControllers;

import com.thirstygoat.kiqo.command.*;
import com.thirstygoat.kiqo.model.AcceptanceCriteria;
import com.thirstygoat.kiqo.model.AcceptanceCriteria.State;
import com.thirstygoat.kiqo.model.Story;
import com.thirstygoat.kiqo.viewModel.AcceptanceCriteriaListCell;
import com.thirstygoat.kiqo.viewModel.MainController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.*;

public class StoryDetailsPaneController implements Initializable, IDetailsPaneController<Story> {

    private MainController mainController;
    private Story story;
    private Map<State, Image> images;
    
    @FXML
    private Label shortNameLabel;
    @FXML
    private Label longNameLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label creatorLabel;
    @FXML
    private Label priorityLabel;
    @FXML
    private Label storyEstimateSliderLabel;
    @FXML
    private ListView<AcceptanceCriteria> acListView;
    @FXML
    private Slider storyEstimateSlider;
    @FXML
    private Button addACButton;
    @FXML
    private Button removeACButton;
    @FXML
    private Button editACButton;


    @Override
    public void showDetails(final Story story) {
        this.story = story;
        if (story != null) {
            longNameLabel.textProperty().bind(story.longNameProperty());
            shortNameLabel.textProperty().bind(story.shortNameProperty());
            descriptionLabel.textProperty().bind(story.descriptionProperty());
            // This is some seriously cool binding
            // Binding to a property of a property
            creatorLabel.textProperty().bind(Bindings.select(story.creatorProperty(), "shortName"));
            priorityLabel.textProperty().bind(Bindings.convert(story.priorityProperty()));
            setScale();

        } else {
            longNameLabel.textProperty().unbind();
            shortNameLabel.textProperty().unbind();
            descriptionLabel.textProperty().unbind();
            creatorLabel.textProperty().unbind();
            priorityLabel.textProperty().unbind();

            longNameLabel.setText(null);
            shortNameLabel.setText(null);
            descriptionLabel.setText(null);
            creatorLabel.setText(null);
            priorityLabel.setText(null);
            storyEstimateSliderLabel.setText(null);
        }

        acListView.setCellFactory(param -> new AcceptanceCriteriaListCell(param, images));
        
        removeACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isEqualTo(0));
        editACButton.disableProperty().bind(Bindings.size(acListView.getSelectionModel().getSelectedItems()).isNotEqualTo(1));
        acListView.setItems(story.getAcceptanceCriteria());

        addACButton.setOnAction(event -> mainController.createAC());
        removeACButton.setOnAction(event -> deleteAC());
        editACButton.setOnAction(event -> mainController.editAC(acListView.getSelectionModel().getSelectedItem()));
    }
    
    private void deleteAC() {
        Command command;
        if (acListView.getSelectionModel().getSelectedItems().size() > 1) {
            // Then we have to deal with a multi AC deletion
            List<Command<?>> commands = new ArrayList<>();
            for (AcceptanceCriteria ac : acListView.getSelectionModel().getSelectedItems()) {
                commands.add(new DeleteAcceptanceCriteriaCommand(ac, story));
            }
            command = new CompoundCommand("Delete Acceptance Criteria", commands);
        } else {
            final AcceptanceCriteria acceptanceCriteria = acListView.getSelectionModel().getSelectedItem();
            command = new DeleteAcceptanceCriteriaCommand(acceptanceCriteria, story);
        }
        mainController.doCommand(command);
    }

    @Override
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        images = new HashMap<>();
        final int IMAGE_SIZE = 20;
        final ClassLoader classLoader = getClass().getClassLoader();
        images.put(State.ACCEPTED, new Image(classLoader.getResourceAsStream("images/acceptedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.REJECTED, new Image(classLoader.getResourceAsStream("images/rejectedState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        images.put(State.NEITHER, new Image(classLoader.getResourceAsStream("images/noState.png"), IMAGE_SIZE, IMAGE_SIZE, false, false));
        initSlider();
        acListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void initSlider() {
        storyEstimateSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() <= story.getScale().getEstimates().length)
                // change the label
                storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(newValue.intValue())]);
        });

        storyEstimateSlider.setOnMouseReleased(event -> {
            if (story.getEstimate() != storyEstimateSlider.getValue()) {
                EditCommand<Story, Integer> editCommand = new EditCommand<>(story, "estimate", (int) storyEstimateSlider.getValue());
                UndoManager.getUndoManager().doCommand(editCommand);
            }
        });
    }

    private void setScale() {
        storyEstimateSlider.setValue(story.getEstimate());
        storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue()]);
        story.scaleProperty().addListener((observable1, oldValue1, newValue1) -> {
            double oldMax = storyEstimateSlider.getMax();
            double newMax = story.getScale().getEstimates().length;
//            int newPos = (int) ((storyEstimateSlider.getValue() / oldMax) * newMax); Will be used later

            if (storyEstimateSlider.getValue() > newMax - 1) {
                storyEstimateSlider.setValue(newMax - 1);
            }
            storyEstimateSliderLabel.setText(story.getScale().getEstimates()[(int) storyEstimateSlider.getValue()]);
            storyEstimateSlider.setMax(newMax - 1);
        });

        story.estimateProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(newValue);
            storyEstimateSlider.setValue(newValue.intValue());
        });
    }
}
