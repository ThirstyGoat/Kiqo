package com.thirstygoat.kiqo.gui.menuBar;

import java.io.IOException;
import java.util.logging.*;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.*;

import com.thirstygoat.kiqo.command.UndoManager;
import com.thirstygoat.kiqo.gui.*;
import com.thirstygoat.kiqo.gui.SideBarController.TabOption;
import com.thirstygoat.kiqo.gui.formControllers.FormController;

import de.saxsys.mvvmfx.ViewModel;

public class MenuBarViewModel implements ViewModel {
    private static final Logger LOGGER = Logger.getLogger(MenuBarViewModel.class.getSimpleName());
    private MainController mainController;
    private BooleanProperty mainControllerIsSet;
    private BooleanProperty changesSaved;
    private BooleanProperty itemIsSelected;
    private BooleanProperty undoDisabled;
    private StringProperty undoString;
    private BooleanProperty redoDisabled;
    private StringProperty redoString;

    private BooleanProperty listIsVisible;
    private BooleanProperty toolBarIsVisible;
    private ObjectProperty<SideBarController.TabOption> selectedTab;

    public MenuBarViewModel() {
        mainControllerIsSet = new SimpleBooleanProperty(false);
        changesSaved = new SimpleBooleanProperty(false);
        itemIsSelected = new SimpleBooleanProperty(false);
        undoDisabled = new SimpleBooleanProperty(true);
        undoString = new SimpleStringProperty("Undo ");
        redoDisabled = new SimpleBooleanProperty(true);
        redoString = new SimpleStringProperty("Redo ");

        listIsVisible = new SimpleBooleanProperty(true);
        toolBarIsVisible = new SimpleBooleanProperty(false);
        selectedTab = new SimpleObjectProperty<>(SideBarController.TabOption.PROJECTS);
    }

    public void setListenersOnUndoManager(UndoManager undoManager) {
        undoString.bind(Bindings.concat("Undo ", undoManager.undoTypeProperty));
        undoDisabled.bind(Bindings.equal("Undo ", undoString));
        redoString.bind(Bindings.concat("Redo ", undoManager.redoTypeProperty));
        redoDisabled.bind(Bindings.equal("Redo ", redoString));
    }

    public void newOrganisationAction() {
        mainController.newOrganisation();
    }

    public void newProjectAction() {
        mainController.newProject();
    }

    public void newReleaseAction() {
        mainController.newRelease();
    }

    public void newSprintAction() {
        mainController.newSprint();
    }

    public void newTeamAction() {
        mainController.newTeam();
    }

    public void newPersonAction() {
        mainController.newPerson();
    }

    public void newSkillAction() {
        mainController.newSkill();
    }

    public void newBacklogAction() {
        mainController.newBacklog();
    }

    public void newStoryAction() {
        mainController.newStory();
    }

    public void undoAction() {
        mainController.undo();
    }

    public void redoAction() {
        mainController.redo();
    }

    public void revertAction() {
        mainController.promptBeforeRevert();
    }

    public void searchAction() {
        mainController.search();
    }

    public void generateReportAction() {
        mainController.statusReport();
    }

    public void openAction() {
        mainController.openOrganisation(null);
    }

    public void saveAction() {
        mainController.saveOrganisation(false);
    }

    public void saveAsAction() {
        mainController.saveOrganisation(true);
    }

    public void quitAction() {
        mainController.exit();
    }

    public void editAction() {
        mainController.editItem();
    }

    public void deleteAction() {
        mainController.deleteItem();
    }

    public void advancedSearchAction() {
        mainController.getDetailsPaneController().showSearchPane();
    }

    public void projectsTabAction() {
        mainController.getSideBarController().show(SideBarController.TabOption.PROJECTS);
    }

    public void teamsTabAction() {
        mainController.getSideBarController().show(SideBarController.TabOption.TEAMS);
    }

    public void peopleTabAction() {
        mainController.getSideBarController().show(SideBarController.TabOption.PEOPLE);
    }

    public void skillsTabAction() {
        mainController.getSideBarController().show(SideBarController.TabOption.SKILLS);
    }

    public MainController getMainController() {
        return mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        mainControllerIsSet.set(true);
        changesSaved.bind(mainController.changesSavedProperty());
        itemIsSelected.bind(Bindings.isNotNull(MainController.focusedItemProperty));
        listIsVisible.addListener((observable, oldValue, newValue) -> {
            mainController.setListVisible(newValue);
        });
        selectedTab.bind(mainController.getSideBarController().selectedTabProperty());
    }

    public BooleanProperty mainControllerIsSet() {
        return mainControllerIsSet;
    }

    public BooleanProperty changesSaved() {
        return changesSaved;
    }

    public BooleanProperty itemSelected() {
        return itemIsSelected;
    }

    public BooleanProperty undoDisabled() {
        return undoDisabled;
    }

    public StringProperty undoString() {
        return undoString;
    }

    public BooleanProperty redoDisabled() {
        return redoDisabled;
    }

    public StringProperty redoString() {
        return redoString;
    }

    public BooleanProperty listVisible() {
        return listIsVisible;
    }

    public BooleanProperty toolBarIsVisible() {
        return toolBarIsVisible;
    }

    public ObjectProperty<TabOption> selectedTab() {
        return selectedTab;
    }

    protected void aboutAction() {
        final Stage stage = new Stage();
        stage.initOwner(MainController.getPrimaryStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("About Kiqo");
        
        final FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MenuBarViewModel.class.getClassLoader().getResource("com/thirstygoat/kiqo/gui/menuBar/About.fxml"));
        Pane root;
        try {
            root = loader.load();
        } catch (final IOException e) {
            MenuBarViewModel.LOGGER.log(Level.SEVERE, "Can't load fxml", e);
            return;
        }
        ((AboutController) loader.getController()).setCloseAction(stage::close);
        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}