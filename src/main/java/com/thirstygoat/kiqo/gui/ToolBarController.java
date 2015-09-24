package com.thirstygoat.kiqo.gui;

import com.thirstygoat.kiqo.command.UndoManager;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;


public class ToolBarController implements Initializable {
    private MainController mainController;
    @FXML
    private Button openButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button newButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button reportButton;
    @FXML
    private ToolBar toolBar;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        initializeButtons();
        addMenuItemHandlers();
        setListenersOnUndoManager();

    }

    private void initializeButtons() {

        Button projectButton = new Button();
        FontAwesomeIconView projectIcon = new FontAwesomeIconView(FontAwesomeIcon.ROCKET);
        projectButton.setGraphic(projectIcon);
        projectIcon.setGlyphSize(20);
        projectButton.setText("Project");
        projectButton.setPrefSize(60,60);
        projectButton.getStyleClass().add("toolbar-newbutton");
        projectButton.setContentDisplay(ContentDisplay.TOP);
        projectButton.setOnAction(event -> {
            mainController.newProject();
        });

        Button personButton = new Button();
        FontAwesomeIconView personIcon = new FontAwesomeIconView(FontAwesomeIcon.USER);
        personButton.setGraphic(personIcon);
        personIcon.setGlyphSize(20);
        personButton.setText("Person");
        personButton.setPrefSize(60, 60);
        personButton.getStyleClass().add("toolbar-newbutton");
        personButton.setContentDisplay(ContentDisplay.TOP);
        personButton.setOnAction(event -> {
            mainController.newPerson();
        });

        Button skillButton = new Button();
        FontAwesomeIconView skillIcon = new FontAwesomeIconView(FontAwesomeIcon.PUZZLE_PIECE);
        skillButton.setGraphic(skillIcon);
        skillIcon.setGlyphSize(20);
        skillButton.setText("Skill");
        skillButton.setPrefSize(60, 60);
        skillButton.getStyleClass().add("toolbar-newbutton");
        skillButton.setContentDisplay(ContentDisplay.TOP);
        skillButton.setOnAction(event -> {
            mainController.newSkill();
        });
        Button teamButton = new Button();
        FontAwesomeIconView teamIcon = new FontAwesomeIconView(FontAwesomeIcon.USERS);
        teamButton.setGraphic(teamIcon);
        teamIcon.setGlyphSize(20);
        teamButton.setText("Team");
        teamButton.setPrefSize(60, 60);
        teamButton.getStyleClass().add("toolbar-newbutton");
        teamButton.setContentDisplay(ContentDisplay.TOP);
        teamButton.setOnAction(event -> {
            mainController.newTeam();
        });

        Button releaseButton = new Button();
        FontAwesomeIconView releaseIcon = new FontAwesomeIconView(FontAwesomeIcon.CALENDAR);
        releaseButton.setGraphic(releaseIcon);
        releaseIcon.setGlyphSize(20);
        releaseButton.setText("Release");
        releaseButton.setPrefSize(60,60);
        releaseButton.getStyleClass().add("toolbar-newbutton");
        releaseButton.setContentDisplay(ContentDisplay.TOP);
        releaseButton.setOnAction(event -> {
            mainController.newRelease();
        });
        Button storyButton = new Button();
        FontAwesomeIconView storyIcon = new FontAwesomeIconView(FontAwesomeIcon.BOOK);
        storyButton.setGraphic(storyIcon);
        storyIcon.setGlyphSize(20);
        storyButton.setText("Story");
        storyButton.setPrefSize(60, 60);
        storyButton.getStyleClass().add("toolbar-newbutton");
        storyButton.setContentDisplay(ContentDisplay.TOP);
        storyButton.setOnAction(event -> {
            mainController.newStory();
        });

        Button backlogButton = new Button();
        FontAwesomeIconView backlogIcon = new FontAwesomeIconView(FontAwesomeIcon.LIST);
        backlogButton.setGraphic(backlogIcon);
        backlogIcon.setGlyphSize(20);
        backlogButton.setText("Backlog");
        backlogButton.setPrefSize(60, 60);
        backlogButton.getStyleClass().add("toolbar-newbutton");
        backlogButton.setContentDisplay(ContentDisplay.TOP);
        backlogButton.setOnAction(event -> {
            mainController.newBacklog();
        });

        Button sprintButton = new Button();
        FontAwesomeIconView sprintIcon = new FontAwesomeIconView(FontAwesomeIcon.FLAG_CHECKERED);
        sprintButton.setGraphic(sprintIcon);
        sprintIcon.setGlyphSize(20);
        sprintButton.setText("Sprint");
        sprintButton.setPrefSize(60,60);
        sprintButton.getStyleClass().add("toolbar-newbutton");
        sprintButton.setContentDisplay(ContentDisplay.TOP);
        sprintButton.setOnAction(event -> {
            mainController.newSprint();
        });

        PopOver newItemPopOver = new PopOver();
        newItemPopOver.setDetachable(false);
        newItemPopOver.setArrowLocation(PopOver.ArrowLocation.TOP_LEFT);

        GridPane gridPane = new GridPane();

        gridPane.add(projectButton, 0, 0);
        gridPane.add(personButton, 1, 0);
        gridPane.add(skillButton, 2, 0);
        gridPane.add(teamButton, 0, 1);
        gridPane.add(releaseButton, 1, 1);
        gridPane.add(storyButton, 2, 1);
        gridPane.setHgrow(storyButton, Priority.ALWAYS);
        gridPane.add(backlogButton, 0, 2);
        gridPane.add(sprintButton, 1, 2);

        ColumnConstraints column = new ColumnConstraints();
        gridPane.getColumnConstraints().addAll(column, column, column);
        column.setHalignment(HPos.CENTER);


        newItemPopOver.setContentNode(gridPane);
        newButton.setOnAction(event -> newItemPopOver.show(newButton));
        newItemPopOver.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                newItemPopOver.hide();
            }
        });

    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    private void addMenuItemHandlers() {
        undoButton.setOnAction(event -> mainController.undo());
        redoButton.setOnAction(event -> mainController.redo());

        reportButton.setOnAction(event -> mainController.statusReport());
        openButton.setOnAction(event -> mainController.openOrganisation(null));
        saveButton.setOnAction(event -> mainController.saveOrganisation(false));
    }



    private void setListenersOnUndoManager() {
        undoButton.disableProperty().bind(Bindings.equal("", UndoManager.getUndoManager().undoTypeProperty));
        redoButton.disableProperty().bind(Bindings.equal("", UndoManager.getUndoManager().redoTypeProperty));
    }

    public void setVisible(Boolean state) {
        toolBar.setVisible(state);
        toolBar.setManaged(state);
    }
}